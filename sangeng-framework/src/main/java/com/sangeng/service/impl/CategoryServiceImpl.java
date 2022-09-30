package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.vo.CategoryVo;
import com.sangeng.mapper.CategoryMapper;
import com.sangeng.service.ArticleService;
import com.sangeng.service.CategoryService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2022-09-13 09:40:08
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private ArticleService articleService;
    /**
     * 实现展示所有分类，这里采用单表查询的方式进行
     * @return
     */
    @Override
    public ResponseResult getCategoryList() {
        //由于采用单表查询的方式，因此应当先查出status为已发布的文章
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //通过过滤条件获得到相应地包含article对象的list集合，并只取内部对象的种类id，并转换为set集合（去掉重复的种类id）
        Set<Long> categoryIds = articleService.list(queryWrapper).stream()
                .map(obj -> obj.getCategoryId()).collect(Collectors.toSet());
        //根据查出来的种类id列表，去查找种类id的相关信息
        List<Category> categories = listByIds(categoryIds);
        //同样需要保证种类的状态是正常状态（0）
        categories = categories.stream()
                .filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus())).collect(Collectors.toList());
        //由于向前端只需要传递种类的id和name属性，于是创建相应的vo对象，并将种类对象转换为vo对象
        List<CategoryVo> result = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);
        //封装成响应对象并返回
        return ResponseResult.okResult(result);
    }

    @Override
    public ResponseResult listAllCategory() {
        //创建基本过滤条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //选出现在还处于正常状态的种类
        queryWrapper.eq(Category::getStatus,SystemConstants.STATUS_NORMAL);
        //根据过滤条件筛选出记录数
        List<Category> categoryList = list(queryWrapper);
        //Bean拷贝至CategoryVo对象中
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class);
        //封装在data中，返回前端
        return ResponseResult.okResult(categoryVos);

    }
}

