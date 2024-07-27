package com.shiro.sojbackendjudgeservice.strategy;


import com.shiro.sojbackendmodel.model.codesandbox.JudgeInfo;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理(简化判题服务)
 */
@Service
public class JudgeManager {

    /**
     * 判题
     * @param judgeContext 上下文
     * @return 判题结果
     */
    public JudgeInfo judge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.judge(judgeContext);
    }
}
