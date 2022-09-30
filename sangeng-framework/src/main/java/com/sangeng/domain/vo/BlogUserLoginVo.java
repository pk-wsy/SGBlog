package com.sangeng.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogUserLoginVo {

    //token
    private String token;

    //用户信息
    private UserInfoVo userInfo;
}
