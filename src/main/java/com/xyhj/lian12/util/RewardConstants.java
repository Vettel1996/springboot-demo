//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.util;

import java.util.HashMap;
import java.util.Map;

public class RewardConstants {
    public static String ACTIVITY_TURNTABLE = "101";        //转盘
    public static String ACTIVITY_BOX = "102";              //宝箱
    public static String ACTIVITY_EGG = "103";              //金蛋

    public static int ACTIVITY_TURNTABLE_POINT = 200;       //转盘消耗积分
    public static int ACTIVITY_BOX_EGG_POINT = 300;         //宝箱金丹消耗积分

    public static String PROP_GOLD = "2";                   //金道具
    public static String PROP_SILVER = "1";                 //银道具

    public static String REWARD_AGAIN = "11";               //再来一次

    public static String ADDRESS_SET_SUCCESS = "1";         //设置地址成功
    public static String ADDRESS_SET_FAILED = "-1";         //设置地址失败

    public static long MILLI_NANO_TIME = 1000000000L;
    public static long TIME_OUT = 2000L;

    public static int USER_IS_AUTH_0 = 0;                   //0:待认证
    public static int USER_IS_AUTH_1 = 1;                   //1：认证中
    public static int USER_IS_AUTH_2 = 2;                   //2：已认证

    public static int REWARD_STATUS_0 = 0;                  //领取
    public static int REWARD_STATUS_1 = 1;                  //待发放
    public static int REWARD_STATUS_2 = 2;                  //已领取

    public static long REWARD_ID_1 = 1L;                    //0.26mBTC
    public static long REWARD_ID_2 = 2L;                    //0.13mBTC
    public static long REWARD_ID_3 = 3L;                    // T-shirt
    public static long REWARD_ID_4 = 4L;                    // 100积分
    public static long REWARD_ID_5 = 5L;                    // 200
    public static long REWARD_ID_6 = 6L;                    // 300
    public static long REWARD_ID_7 = 7L;                    // 再来一次
    public static long REWARD_ID_8 = 8L;                    // 金锤子
    public static long REWARD_ID_9 = 9L;                    // 银锤子
    public static long REWARD_ID_0 = 10L;                   // 金钥匙
    public static long REWARD_ID_11 = 11L;                  // 银钥匙

    public static int ADDRESS_DEFAULT_0 = 0;                //默认地址 否
    public static int ADDRESS_DEFAULT_1 = 1;                //默认地址 是

    public static int REWARD_TYPE_1 = 1;                    //BTC
    public static int REWARD_TYPE_2 = 2;                    //t-shirt
    public static int REWARD_TYPE_3 = 3;                    //积分
    public static int REWARD_TYPE_11 = 11;                  //再来一次
    public static int REWARD_TYPE_12 = 12;                  //谢谢参与
    public static int REWARD_TYPE_13 = 13;                  //金锤子
    public static int REWARD_TYPE_14 = 14;                  //银钥匙
    public static int REWARD_TYPE_15 = 15;                  //金钥匙
    public static int REWARD_TYPE_16 = 16;                  //银钥匙
    public static Map<Integer, Integer> TPYE_WEIGHT_MAP = new HashMap<Integer, Integer>() {
        {
            this.put(1,200);         //比特币
            this.put(2,25);          //t-shirt
            this.put(3,1000);        //积分
            this.put(11,1975);       //再来一次
            this.put(12,3200);       //谢谢参与
            this.put(13,600);        //金锤子
            this.put(14,600);        //银锤子
            this.put(15,600);        //金钥匙
            this.put(16,600);        //银钥匙
        }
    };

    public RewardConstants() {
    }
}
