package com.shiro.sojbackendjudgeservice.strategy;


import com.shiro.sojbackendmodel.model.codesandbox.JudgeInfo;
import com.shiro.sojbackendmodel.model.dto.question.JudgeCase;
import com.shiro.sojbackendmodel.model.entity.Question;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private Question question;

    private List<JudgeCase> judgeCaseList;

    private QuestionSubmit questionSubmit;
}
