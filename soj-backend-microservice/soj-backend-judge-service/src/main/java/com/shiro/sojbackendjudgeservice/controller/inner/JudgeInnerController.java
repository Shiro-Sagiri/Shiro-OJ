package com.shiro.sojbackendjudgeservice.controller.inner;

import com.shiro.sojbackendjudgeservice.service.JudgeService;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import com.shiro.sojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    /**
     * 判题
     *
     * @param questionSubmitId 题目提交id
     * @return 判题结果
     */
    @PostMapping("/judge")
    @Override
    public QuestionSubmit judge(@RequestParam("questionSubmitId") Long questionSubmitId) {
        return judgeService.judge(questionSubmitId);
    }
}
