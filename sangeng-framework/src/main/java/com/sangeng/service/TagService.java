package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.TagListDto;
import com.sangeng.domain.dto.TagUpdateDto;
import com.sangeng.domain.entity.Tag;
import com.sangeng.domain.vo.PageVo;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2022-09-22 13:40:10
 */
public interface TagService extends IService<Tag> {

    ResponseResult<PageVo> pageTageList(Integer pageNum, Integer pageSize, TagListDto tagListDto);

    ResponseResult addTag(TagListDto tagListDto);

    ResponseResult deleteTag(String id);

    ResponseResult getTagById(String id);

    ResponseResult updateTageById(TagUpdateDto tagUpdateDto);

    ResponseResult listAllTag();
}

