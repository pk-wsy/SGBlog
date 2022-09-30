package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.entity.ArticleTag;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.vo.ArticleListVo;
import com.sangeng.domain.vo.DetailArticleVo;
import com.sangeng.domain.vo.HotArticleVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.service.ArticleService;
import com.sangeng.mapper.ArticleMapper;
import com.sangeng.service.ArticleTagService;
import com.sangeng.service.CategoryService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
* @author pc
* @description 针对表【sg_article(文章表)】的数据库操作Service实现
* @createDate 2022-09-13 09:29:21
*/
@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleTagService articleTagService;
    /**
     * 实现查找浏览量排行到前10的热门文章
     *
     * @return
     */
    @Override
    public ResponseResult<HotArticleVo> hotArticleList() {

        //需求：查询前10篇文章，按照浏览量view_count排序，status状态应该为0表示已发布，删除标记del_flag应为0表示未删除
        //由于在yml文件中配置了逻辑删除，因此可以不用处理del_flag的要求
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL)
                        .orderByDesc(Article::getViewCount);
        //创建Page对象，设置为第1页，并且每页十条。这样可以保证显示的是前十的记录
        Page<Article> page = new Page<>(1,10);
        //调用MybatisPlus提供的page()方法，将Wrapper筛选后的结果封装到page对象中
        page(page,queryWrapper);

        //得到封装好的Page对象后，查看它存储的数据
        List<Article> records = page.getRecords();
        //需要在前端展示该article对象的id（用于用户点击此条时可以显示该文章详细信息）、title(显示该文章标题)以及viewCount(根据浏览量排序肯定显示浏览量)
        //由于目前的Article对象包含了所有字段，而我们无需传输那么多字段，因此将List中的Article对象封装为对应的VO对象
        //由于这个操作有一定的代码量，并且其他功能中也会使用，因此考虑将其封装起来
        List<HotArticleVo> data = BeanCopyUtils.copyBeanList(records, HotArticleVo.class);

        //封装到统一的响应对象中，调用该响应类的静态方法则无需创建该类对象
        return ResponseResult.okResult(data);

    }

    @Override
    public ResponseResult articleById(Long id) {
        Article article = getById(id);
        // 对于article对象，我们规定这个对象的viewCount属性应当从redis中读取，而不是从mysql数据库中进行读取
        // 因此应当调用我们自己封装的redisCache类来对redis中的数据进行读取
        // 这个方法需要传递两个参数，第一个参数是map集合在redis中对应的key值，第二个参数是map集合中entry键值对对应的key值
        Integer viewCount = redisCache.getCacheMapValue(SystemConstants.REDIS_VIEW_COUNT_KEY, id.toString());
        //存入到article对象
        article.setViewCount(viewCount.longValue());
        //由于article对象中是category_id，我们要查询的是category_name，因此需要借助categoryService进行查询
        //创建过滤条件对象
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //创建categoryId与category表中id相等的条件，并根据该条件得到category对象
        Category category = categoryService.getOne(queryWrapper.eq(Category::getId, article.getCategoryId()));
        //将categoryName值注入到article对象中，记得sg_article表中没有的字段要用@TableField(exist=false)来标识
        article.setCategoryName(category.getName());
        //将article对象中的属性bean拷贝至VO对象中,创建对应的VO对象
        DetailArticleVo detailArticleVo = BeanCopyUtils.copyBean(article, DetailArticleVo.class);
        //封装在统一的响应体中
        return ResponseResult.okResult(detailArticleVo);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //创建过滤条件的对象
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        /*1. 分类要和传来的分类id相同
          2. 文章状态应该为已发布状态
          3. 对isTop字段降序排序
         */

        //应该先判断前端是否传来种类id，如果传来了，需要按照分类进行处理
        queryWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0,Article::getCategoryId,categoryId);
        //再进行判断后两个选项
        queryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL)
        .orderByDesc(Article::getIsTop);


        //创建Page对象
        Page<Article> page = new Page<>(pageNum,pageSize);
        //将数据封装到page对象中
        page(page, queryWrapper);
        //从page对象中得到结果集
        List<Article> records = page.getRecords();
        //查询categoryName字段，需要调用categoryService查询
        records.stream().map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());

        //去除多余字段，bean拷贝至Vo对象
        List<ArticleListVo> result = BeanCopyUtils.copyBeanList(records, ArticleListVo.class);
        //由于是分页需求，前端要求返还两个属性，rows属性保存对应数据,total返回总记录数
        //因此，再进行一层封装，由于这个Vo对象在日后的分页需求都为这两个属性，因此考虑将其放在公共部分，并用PageVo来进行标识
        PageVo<ArticleListVo> pageVo = new PageVo<ArticleListVo>(result,page.getTotal());
        //封装成为统一的返还格式
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新浏览量数据
        redisCache.incrementCacheMapValue(SystemConstants.REDIS_VIEW_COUNT_KEY, id.toString(), 1);
        //返回响应信息
        return ResponseResult.okResult();
    }

    @Override
    // 由于在这个service层方法中，调用了两个及以上对数据库进行写操作的方法，因此需要保证要么同时成功要么同时失败
    // 通过事务的注解来实现
    @Transactional
    public ResponseResult addArticle(AddArticleDto addArticleDto) {
        //将Dto对象通过bean拷贝至Article对象
        Article article = BeanCopyUtils.copyBean(addArticleDto, Article.class);
        //将article对象保存至数据库中
        save(article);
        //由于articleDto对象中还有List<tag>属性，这个表示的是article与tag之间的对应关系。是多对多关系
        //因此应当存入到sg_article_tag表中（需要调用对应的service）
        //得到对应的标签id集合
        List<Long> tags = addArticleDto.getTags();
        //通过stream流实现
         articleTagService.saveBatch(tags.stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList()));
         //返回响应给前端
        return ResponseResult.okResult();
    }


}




