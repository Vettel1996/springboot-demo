//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.vo;

import com.xyhj.lian12.user.dto.Page;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserIncomeConsumeVO extends Page {
    private String time;        //创建时间
    private String type;        //来源
    private String number;      //数量
    private Integer status;     //收入支出标识
    private Long point;         //总积分
}
