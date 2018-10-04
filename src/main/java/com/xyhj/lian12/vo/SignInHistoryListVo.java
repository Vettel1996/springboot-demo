//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SignInHistoryListVo implements Serializable {
    private static final long serialVersionUID = 8785240965361870156L;
    public static Integer SIGN_STATUS_NO = 0;
    public static Integer SIGN_STATUS_yes = 1;
    private String reward;
    private Integer status;
    @JsonFormat(
            pattern = "yyyy-MM-dd"
    )
    private LocalDateTime time;
    private String url;
    private String currentDay;
}
