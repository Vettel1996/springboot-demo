//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RewardWinVO {
    private String reward;      //奖品名称
    private Long rwinid;        //获奖记录id
    private Integer status;     //状态
    private String time;        //获奖时间
    private String type;        //获奖来源
    private String rewardType;  //奖品类型
}
