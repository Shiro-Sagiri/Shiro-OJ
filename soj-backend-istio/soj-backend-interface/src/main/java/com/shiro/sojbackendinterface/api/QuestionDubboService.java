package com.shiro.sojbackendinterface.api;

import com.shiro.sojbackendmodel.model.entity.Question;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;

public interface QuestionDubboService {
    QuestionSubmit getQuestionSubmitById(Long questionSubmitId);

    Question getQuestionById(Long questionId);

    boolean updateQuestionSubmitById(QuestionSubmit questionSubmitUpdate);

    void updateQuestionById(Question question);
}
