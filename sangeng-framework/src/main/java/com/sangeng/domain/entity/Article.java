package com.sangeng.domain.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文章表
 * @TableName sg_article
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sg_article")
//添加该注解，可以保证set方法返还该对象本身
@Accessors(chain = true)
public class Article implements Serializable {
    /**
     * 
     */
    @TableId
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 所属分类id
     */
    private Long categoryId;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 是否置顶（0否，1是）
     */
    private String isTop;

    /**
     * 状态（0已发布，1草稿）
     */
    private String status;

    /**
     * 访问量
     */
    private Long viewCount;

    /**
     * 是否允许评论 1是，0否
     */
    private String isComment;

    /**
     * 设置为自动填充，之后进行写博文功能时MP可以自动增加此字段
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 设置为自动填充，之后进行写博文功能时MP可以自动增加此字段
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 设置为自动填充，之后进行写博文功能时MP可以自动增加此字段
     * 也可以在修改时自动修改此字段
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 设置为自动填充，之后进行写博文功能时MP可以自动增加此字段
     * 也可以在修改时自动修改此字段
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志（0代表未删除，1代表已删除）
     */
    private Integer delFlag;

    /**
     * 存储种类名称，由于对应的表中没有该字段，注意进行标注
     */
    @TableField(exist = false)
    private String categoryName;


    public Article(Long id, Long viewCount){
        this.id = id;
        this.viewCount = viewCount;
    }
}