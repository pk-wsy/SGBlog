package com.sangeng.controller;


import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.TagListDto;
import com.sangeng.domain.dto.TagUpdateDto;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 实现可以根据标签名展示所有tag信息，并且是分页展示（因为标签可能会比较多）
     * 前端也可以传来备注remark，后续也可以增加根据备注remark内容展示相关标签信息
     * 可以考虑将前端传来的名字和备注两个属性封装在一个dto对象中进行接收
     * @return
     */
    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, TagListDto tagListDto){
        //调用service方法，展示符合条件的分页标签信息
        return tagService.pageTageList(pageNum,pageSize,tagListDto);
    }


    /**
     * 实现处理新增标签的需求
     * @param tagListDto
     * @return
     */
    @PostMapping
    public ResponseResult addTag(@RequestBody TagListDto tagListDto){
        //处理增加标签的需求，调用service层方法
        return tagService.addTag(tagListDto);
    }


    /**
     * 实现对于某一个标签的删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteTag(@PathVariable("id") String id){
        return tagService.deleteTag(id);
    }

    /**
     * 实现根据id查找对应标签信息，用于在修改标签功能中前端页面上的回显(修改标签需求的第一个接口)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult getTagById(@PathVariable("id") String id){
        return tagService.getTagById(id);
    }

    /**
     * 实现根据id修改标签信息（修改标签需求的第二个接口）
     * @param tagUpdateDto
     * @return
     */
    @PutMapping
    public ResponseResult updateTagById(@RequestBody TagUpdateDto tagUpdateDto){
        return tagService.updateTageById(tagUpdateDto);
    }

    /**
     * 实现展示出所有的标签信息，返回给前端的tagvo包括id和name字段即可
     * 用于在写博文栏目下点击标签，会弹出包含所有标签名称的下拉框
     * 是实现写博文功能的第二个接口
     * @return
     */
    @GetMapping("/listAllTag")
    public ResponseResult listAllTag(){
        return tagService.listAllTag();
    }

}
