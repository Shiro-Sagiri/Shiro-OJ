package com.shiro.sojbackendquestionservice.controller.inner;

import com.shiro.sojbackendmodel.model.entity.Question;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import com.shiro.sojbackendquestionservice.service.QuestionService;
import com.shiro.sojbackendquestionservice.service.QuestionSubmitService;
import com.shiro.sojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") Long questionId) {
        return questionService.getById(questionId);
    }

    @GetMapping("/getSubmit/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @Override
    @PutMapping("/updateQuestion")
    public boolean updateQuestionById(@RequestBody Question question) {
        return questionService.updateById(question);
    }

    @PutMapping("/updateSubmit")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }
}
