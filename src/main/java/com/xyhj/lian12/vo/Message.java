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
public class Message {
    public static final int CONNECTED_SUCCESS_NOTIFY = 1;
    public static final int GROUP_PUSH_INFO = 2;
    public static final int SINGLE_USER_INFO = 3;
    public static final int GROUP_REWARD_PUSH_INFO = 4;
    public static final int GROUP_BTC_PRICE = 5;
    public static final int RECONNECTED_STATUS = 6;
    public static final int HEART_BEAT = 10;
    public static final Integer HAS_READ = 1;
    public static final Integer NO_READ = 0;
    private Integer sendType;
    // 消息内容
    private Object content;
    private Long msgId;
}
