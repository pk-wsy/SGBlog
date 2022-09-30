package com.sangeng.controller;

import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Comment;
import com.sangeng.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 实现展示某个文章下的评论内容
     * 由于评论内容比较多，我们需要进行分页展示，因此需要前端传来页号和页大小
     * @param articleId 对应的文章id
     * @param pageNum 对应的页号
     * @param pageSize 对应的页大小
     * @return 封装的统一返回格式
     */
    @GetMapping("/commentList")
    public ResponseResult commentList(Long articleId, Integer pageNum, Integer pageSize){
        return commentService.commentList(SystemConstants.ARTICLE_COMMENT,articleId, pageNum, pageSize);
    }


    /**
     * 实现给某一文章或者友链下添加评论（通过comment中type属性进行标识）
     *
     * @param comment
     * @return
     */
    @PostMapping
    public ResponseResult addComment(@RequestBody Comment comment){
        return commentService.addComment(comment);
    }


    /**
     * 实现展示友链模块下的评论内容
     * @param pageNum 页号
     * @param pageSize 页大小
     * @return 封装的统一返回格式
     */
    @GetMapping("/linkCommentList")
    public ResponseResult linkCommentList(Integer pageNum, Integer pageSize){
        //考虑到友链评论和文章评论共用的是一个comment表
        //逻辑差不多，因此只需要用一个参数来标识comment类型即可实现对于友链评论的查询
        //因此复用之前查询文章评论的service层方法，新增一个参数用于标识评论类型
        return commentService.commentList(SystemConstants.LINK_COMMENT,null,pageNum, pageSize);

    }

}
