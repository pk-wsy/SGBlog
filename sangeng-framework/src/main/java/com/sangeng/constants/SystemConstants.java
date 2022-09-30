package com.sangeng.constants;

public class SystemConstants {
    /**
     *  文章是草稿
     */
    public static final int ARTICLE_STATUS_DRAFT = 1;
    /**
     *  文章是正常分布状态
     */
    public static final int ARTICLE_STATUS_NORMAL = 0;


    public static final String  STATUS_NORMAL = "0";

    /**
     * 友链是正常状态
     */
    public static final String LINK_STATUS_NORMAL = "0";

    /**
     * 用户为正常状态
     */
    public static final String USER_STATUS_NORMAL = "0";

    /**
     * 用于存入redis的id前缀(前台服务)
     */
    public static final String ID_PREFIX = "bloglogin";

    /**
     * 用于存入redis的id前缀（后台服务）
     */
    public static final String ID_PREFIX_BACK = "login";
    /**
     * 标识是根评论
     */
    public static final String PARENT_ROOT_ID = "-1";

    /**
     * 表示是文章评论
     */
    public static final String ARTICLE_COMMENT = "0";

    /**
     * 表示是友链评论
     */
    public static final String LINK_COMMENT = "1";

    /**
     * 浏览量在redis中的key
     */
    public static final String REDIS_VIEW_COUNT_KEY="article:viewCount";

    /**
     * 权限类型是M
     */
    public static final String MENU_TYPE_M = "M";

    /**
     * 权限类型是C
     */
    public static final String MENU_TYPE_C= "C";

    /**
     * 权限类型是F
     */
    public static final String MENU_TYPE_F= "F";

    /**
     * 该用户是后台用户
     */
    public static final String ADMIN_USER = "1";
}
