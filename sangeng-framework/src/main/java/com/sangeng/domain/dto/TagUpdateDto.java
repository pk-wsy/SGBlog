package com.sangeng.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagUpdateDto {

    private Long id;

    //标签属性
    private String name;

    //标签备注
    private String remark;
}
