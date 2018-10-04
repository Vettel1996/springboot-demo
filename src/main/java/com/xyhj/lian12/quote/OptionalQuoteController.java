//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.quote;

import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.quote.interfaces.QuoteRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"optional"})
public class OptionalQuoteController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(OptionalQuoteController.class);
    @Autowired
    private QuoteRpc quoteRpc;

    public OptionalQuoteController() {
    }
}
