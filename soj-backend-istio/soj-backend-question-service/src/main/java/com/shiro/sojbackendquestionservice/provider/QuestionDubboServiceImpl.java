package com.shiro.sojbackendquestionservice.provider;

import com.shiro.sojbackendinterface.api.QuestionDubboService;
import com.shiro.sojbackendmodel.model.entity.Question;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import com.shiro.sojbackendquestionservice.service.QuestionService;
import com.shiro.sojbackendquestionservice.service.QuestionSubmitService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public class QuestionDubboServiceImpl implements QuestionDubboService {

    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    public QuestionSubmit getQuestionSubmitById(Long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return questionService.getById(questionId);
    }

    @Override
    public boolean updateQuestionSubmitById(QuestionSubmit questionSubmitUpdate) {
        return questionSubmitService.updateById(questionSubmitUpdate);
    }

    @Override
    public void updateQuestionById(Question question) {
        questionService.updateById(question);
    }
}
