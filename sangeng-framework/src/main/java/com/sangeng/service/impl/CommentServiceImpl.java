package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Comment;
import com.sangeng.domain.vo.CommentVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.mapper.CommentMapper;
import com.sangeng.service.CommentService;
import com.sangeng.service.UserService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2022-09-18 22:47:14
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;


    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        //创建过滤条件，筛选对应文章或友链下的评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //可以先判断要查找评论是文章还是友链，如果是文章，则需要进一步根据其文章id进行筛选
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId,articleId);
        //根据传来的评论类型参数，查找对应类型的评论
        queryWrapper.eq(Comment::getType,commentType);
        //同时还要满足rootId为-1,查出该文章或友链的对应根评论，然后根据根评论在后续进行子评论的查找与显示
        queryWrapper.eq(Comment::getRootId, SystemConstants.PARENT_ROOT_ID);
        //由于前端传来了分页相关的参数，因此需要对数据进行分页
        Page<Comment> page = new Page<>(pageNum,pageSize);
        //封装到page对象中
        page(page,queryWrapper);
        //得到出对应分页中的数据
        List<Comment> commentList = page.getRecords();
        //将comment对象bean拷贝至commentVo对象当中
        //由于commentVo对象中有子评论children属性，comment对象中不存在此属性，因此需要通过再次查询并赋值才能够得以实现
        //这个过程整体比较繁琐，而且复用性较强，因此我们考虑封装到一个方法中
        List<CommentVo> commentVoList = toCommentVoList(commentList);

        //遍历所有的CommentVo集合，找到根评论CommentVo，如果它们有子评论，则设置children属性
        for(CommentVo commentVo : commentVoList){
            //将赋给children属性值的工作封装在一个方法内部，需要传递标识该commentVo的id，从而查找它下面的子评论
            List<CommentVo> children = getChildren(commentVo.getId());
            commentVo.setChildren(children);
        }
        //由于是分页显示，封装为pageVo对象
        PageVo<CommentVo> pageVo = new PageVo<>(commentVoList,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addComment(Comment comment) {
        //后端对参数进行一定的校验
        //检验是否为空，如果为空则抛出我们自定义的系统异常
        if(!StringUtils.hasText(comment.getContent())){
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        //进行评论的存储工作
        //如果没有任何其他操作，由于前端传来的comment参数属性不全，有部分属性没存在comment对象中，如createTime、createBy等属性
        //因此需要在后端对comment其他属性同样进行赋值，然后一起存入到sg_comment表中
        //调用这个方法会先将comment对象中的字段进行保存
        //也会自动去comment表中寻找是否有字段设置了MP的自动填充，如果发现有fill属性为Insert的字段就会被按照定义好的自动填充逻辑进行自动填充
        save(comment);
        //由于是增加记录，不用返回前端data数据，只需要返回成功信息即可
        return ResponseResult.okResult();
    }


    private List<CommentVo> getChildren(Long id) {
        //创建过滤条件去寻找
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId,id);
        //按照回复时间从早到晚（升序）排列
        queryWrapper.orderByAsc(Comment::getCreateTime);
        //封装至commentVo集合中
        List<CommentVo> commentVos = toCommentVoList(list(queryWrapper));

        return commentVos;
    }

    private List<CommentVo> toCommentVoList(List<Comment> commentList) {
        List<CommentVo> commentVoList = BeanCopyUtils.copyBeanList(commentList, CommentVo.class);
        for(CommentVo commentVo : commentVoList){
            //需要查询对应的评论的用户的昵称，封装在commentVo对象的username属性中
            String nickName = userService.getById(commentVo.getCreateBy()).getNickName();
            commentVo.setUsername(nickName);
            //查询该commentvo对象的tocommentUserId是否为-1
            //如果不为-1，表示存在回复的用户，则应该封装回复的用户的昵称属性（toCommentUserName）
            if(commentVo.getToCommentUserId() != -1){
                commentVo.setToCommentUserName(userService.getById(commentVo.getToCommentUserId()).getNickName());
            }

        }
        return commentVoList;

    }
}

