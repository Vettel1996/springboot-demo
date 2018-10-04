//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.sms;

import com.xyhj.lian12.sms.interfaces.SmsRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/smsReport"})
public class SmsController {
    private static final Logger log = LoggerFactory.getLogger(SmsController.class);
    @Autowired
    private SmsRpc smsRpc;

    public SmsController() {
    }

    @RequestMapping(
            value = {"/report"},
            method = {RequestMethod.GET}
    )
    public void report(@RequestParam(value = "receiver",required = false) String receiver, @RequestParam(value = "pswd",required = false) String pswd, @RequestParam(value = "msgid",required = false) String msgid, @RequestParam(value = "reportTime",required = false) String reportTime, @RequestParam(value = "mobile",required = false) String mobile, @RequestParam(value = "status",required = false) String status) {
        this.smsRpc.report(receiver, pswd, msgid, reportTime, mobile, status);
    }
}
