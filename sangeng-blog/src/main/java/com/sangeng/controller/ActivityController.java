package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.vo.HotArticleVo;
import com.sangeng.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ActivityController {
    @Autowired
    private ArticleService articleService;

    /**
     * 完成展示所有文章的需求
     * @return
     */
    @RequestMapping("/list")
    public List<Article> list(){
        return articleService.list();
    }

    /**
     * 完成展示热度前十文章的需求
     * @return
     */
    @RequestMapping("/hotArticleList")
    public ResponseResult<HotArticleVo> hotArticleList(){
        //因为这个功能在前后台都可以存在，因此调用的service应该放在公共模块中
        //调用公共模块中service层方法，实现这个功能
        return articleService.hotArticleList();
    }

    /**
     * 实现“阅读全文”的需求，即根据某一文章的id显示该文章下的详细内容
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult articleById(@PathVariable("id") Long id){
        return articleService.articleById(id);
    }

    /**
     * 实现分页展示的需求，如果前端点击某一分类，则在该分类下进行分页展示
     * @param pageNum 页码数
     * @param pageSize 页大小
     * @param categoryId 分类id
     * @return 统一返回格式
     */
    @RequestMapping("/articleList")
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId){
        return articleService.articleList(pageNum, pageSize, categoryId);
    }

    /**
     * 更新浏览量数据，进入文章详情的页面后前端会默认调用此接口，实现浏览量更新（更新至redis而非直接更新数据库）
     * @param id 文章id
     * @return
     */
    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        return articleService.updateViewCount(id);
    }
}
