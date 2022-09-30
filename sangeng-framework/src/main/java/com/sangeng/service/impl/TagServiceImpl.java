package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.TagListDto;
import com.sangeng.domain.dto.TagUpdateDto;
import com.sangeng.domain.entity.Tag;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.domain.vo.TagVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.mapper.TagMapper;
import com.sangeng.service.TagService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2022-09-22 13:40:11
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public ResponseResult<PageVo> pageTageList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        // 创建过滤条件对象
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        //先判断前端传来的dto中包含不包含name或者remark，包含再根据这些属性进行筛选
        queryWrapper.eq(StringUtils.hasText(tagListDto.getName()), Tag::getName, tagListDto.getName());
        queryWrapper.eq(StringUtils.hasText(tagListDto.getRemark()), Tag::getRemark, tagListDto.getRemark());

        //创建Page对象
        Page<Tag> page = new Page<>(pageNum, pageSize);
        //调用分页方法，将符合上述条件的tag对象封装在page中
        page(page, queryWrapper);
        //封装到PageVo对象中用于前台返回
        PageVo<Tag> pageVo = new PageVo<>(page.getRecords(), page.getTotal());
        return ResponseResult.okResult(pageVo);
    }


    @Override
    public ResponseResult addTag(TagListDto tagListDto) {
        //后端需要先验证前端传来的数据
        if(tagListDto.getName()== null){
            throw new SystemException(AppHttpCodeEnum.TAG_INFO_NOT_NULL);
        }
        //Bean拷贝至tag对象中
        Tag tag = BeanCopyUtils.copyBean(tagListDto, Tag.class);
        //通过验证后可以新增数据至tag表中，注意要在Tag的四个字段上加上MP自动填充
        save(tag);
        return ResponseResult.okResult();
    }


    @Override
    public ResponseResult deleteTag(String id) {
        //后端进行参数校验
        if(!StringUtils.hasText(id)){
            throw new SystemException(AppHttpCodeEnum.TAG_INFO_NOT_NULL);
        }
        //通过校验后，则删除tag表中相关数据
        boolean b = removeById(id);
        if(b){
            return ResponseResult.okResult();
        }else{
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public ResponseResult getTagById(String id) {
        //后端进行参数校验
        if(!StringUtils.hasText(id)){
            throw new SystemException(AppHttpCodeEnum.TAG_INFO_NOT_NULL);
        }
        //查询对应id的标签信息
        Tag tag = getById(id);
        //Bean拷贝至tagVo对象，并返回
        return ResponseResult.okResult(BeanCopyUtils.copyBean(tag, TagVo.class));
    }

    @Override
    public ResponseResult updateTageById(TagUpdateDto tagUpdateDto) {
        //后端需要先验证前端传来的数据
        if(tagUpdateDto.getName()== null){
            throw new SystemException(AppHttpCodeEnum.TAG_INFO_NOT_NULL);
        }
        //bean拷贝至tag对象
        Tag tag = BeanCopyUtils.copyBean(tagUpdateDto, Tag.class);
        //根据tag对象的id去修改数据库中的值
        updateById(tag);
        //返回响应
        return ResponseResult.okResult();
    }


    @Override
    public ResponseResult listAllTag() {
        //查询所有标签记录,只要Tag对象中的id和name属性
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId,Tag::getName);
        List<Tag> tagList = list(queryWrapper);
        //创建TagVo对象，并且bean拷贝至vo对象内
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(tagList, TagVo.class);
        //返回前端响应
        return ResponseResult.okResult(tagVos);
    }
}

