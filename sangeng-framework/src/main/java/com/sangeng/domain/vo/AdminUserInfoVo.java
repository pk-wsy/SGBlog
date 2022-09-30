package com.sangeng.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AdminUserInfoVo {
    //存放menu表的perms字段
    private List<String> permissions;

    //存放role表的role_key字段
    private List<String> roles;

    //存放用户信息
    private UserInfoVo user;
}
