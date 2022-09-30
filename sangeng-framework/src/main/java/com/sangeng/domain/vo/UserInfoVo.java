package com.sangeng.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class UserInfoVo {

    private Long id;

    //昵称
    private String nickName;

    //邮箱
    private String email;

    //用户性别（0男，1女，2未知）
    private String sex;
    //头像
    private String avatar;
}
