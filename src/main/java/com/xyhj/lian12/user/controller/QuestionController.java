//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xyhj.lian12.user.controller;

import com.xyhj.lian.util.RespCode;
import com.xyhj.lian.util.RespEntity;
import com.xyhj.lian12.base.config.BaseController;
import com.xyhj.lian12.user.dto.UserDto;
import com.xyhj.lian12.user.dto.UserQuestion;
import com.xyhj.lian12.user.dto.UserQuestionReply;
import com.xyhj.lian12.user.interfaces.UserRpc;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/user"})
public class QuestionController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
    @Autowired
    private UserRpc userRpc;

    public QuestionController() {
    }

    /**
     * 提交反馈
     * @param userQuestion
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"/ask"})
    public RespEntity question(UserQuestion userQuestion, HttpServletRequest request) throws Exception {
        UserDto user = (UserDto)request.getAttribute("user");
        if (userQuestion.getDetail().length() > 200) {
            return RespEntity.error(RespCode.QSK_DETAIL_LENGTHERROR);
        } else {
            userQuestion.setUuid(user.getUuid());
            userQuestion.setName(user.getUname());
            return this.userRpc.question(userQuestion);
        }
    }

    @PostMapping({"appendAask"})
    public RespEntity appendAsk(UserQuestionReply userQuestionReply, HttpServletRequest request) throws Exception {
        UserDto user = (UserDto)request.getAttribute("user");
        if (userQuestionReply.getDetail().length() > 490) {
            return RespEntity.error(RespCode.QSK_DETAIL_LENGTHERROR);
        } else {
            userQuestionReply.setManagerId(user.getUid());
            userQuestionReply.setManagerName(user.getEmail());
            return this.userRpc.appendAsk(userQuestionReply, user.getUuid());
        }
    }

    /**
     * 我的提问列表
     * @param userQuestion
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping({"/questions"})
    public RespEntity myQuestions(UserQuestion userQuestion, HttpServletRequest request) throws Exception {
        UserDto user = (UserDto)request.getAttribute("user");
        // 构建查询参数
        userQuestion.setUuid(user.getUuid());
        userQuestion.setStart((userQuestion.getStart() - 1) * Integer.valueOf(userQuestion.getSize()));
        return this.userRpc.myQuestions(userQuestion);
    }

    /**
     * 查询单个问题
     * @param qid
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"/question"})
    public RespEntity question(@RequestParam("qid") Long qid, HttpServletRequest request) throws Exception {
        if (qid != null && !qid.equals("")) {
            UserDto user = (UserDto)request.getAttribute("user");
            log.info("查询问题获取的user{}", user);
            return this.userRpc.question(qid, user.getUuid());
        } else {
            log.error("带进来的参数{}", qid);
            return RespEntity.error(RespCode.SYSTEM_ERROR);
        }
    }

    /**
     * 删除单个问题
     * @param qid
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping({"/question/del"})
    public RespEntity delQuestion(@RequestParam("qid") Long qid, HttpServletRequest request) throws Exception {
        UserDto user = (UserDto)request.getAttribute("user");
        return this.userRpc.delQuestion(qid, user.getUuid());
    }

    /**
     * 查询单个问题详情
     * @param qid
     * @param uid
     * @return
     * @throws Exception
     */
    @PostMapping({"/questionDetail"})
    public RespEntity questionDetail(@RequestParam("qid") Long qid, @RequestParam("uid") Long uid) throws Exception {
        return this.userRpc.questionDetail(qid, uid.toString());
    }
}
