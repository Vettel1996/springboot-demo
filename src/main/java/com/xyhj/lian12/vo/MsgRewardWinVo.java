//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MsgRewardWinVo implements Serializable {
    private static final long serialVersionUID = -8216145545742096311L;
    private String uname;// 用户名称
    private String reward;// 奖品名称
    private Integer precious;
}
