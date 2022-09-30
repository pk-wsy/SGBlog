package com.sangeng.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListVo {
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
     * 文章摘要
     */
    private String summary;


    /**
     * 缩略图
     */
    private String thumbnail;


    private Long viewCount;

    /**
     *
     */
    private Date createTime;



    private String categoryName;
}
