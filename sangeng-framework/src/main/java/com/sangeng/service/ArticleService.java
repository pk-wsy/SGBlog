package com.sangeng.service;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.vo.HotArticleVo;

/**
* @author pc
* @description 针对表【sg_article(文章表)】的数据库操作Service
* @createDate 2022-09-13 09:29:21
*/
public interface ArticleService extends IService<Article> {

    ResponseResult<HotArticleVo> hotArticleList();

    ResponseResult articleById(Long id);


    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult updateViewCount(Long id);

    ResponseResult addArticle(AddArticleDto addArticleDto);
}
