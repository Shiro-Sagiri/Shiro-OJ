package com.shiro.sojbackendjudgeservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.shiro.sojbackendcommon.enums.ErrorCode;
import com.shiro.sojbackendcommon.enums.JudgeInfoMessageEnum;
import com.shiro.sojbackendcommon.enums.QuestionSubmitStatusEnum;
import com.shiro.sojbackendcommon.exception.BusinessException;
import com.shiro.sojbackendinterface.api.QuestionDubboService;
import com.shiro.sojbackendjudgeservice.codeSandBox.CodeSendBox;
import com.shiro.sojbackendjudgeservice.codeSandBox.CodeSendBoxFactory;
import com.shiro.sojbackendjudgeservice.codeSandBox.CodeSendBoxProxy;
import com.shiro.sojbackendjudgeservice.service.JudgeService;
import com.shiro.sojbackendjudgeservice.strategy.JudgeContext;
import com.shiro.sojbackendjudgeservice.strategy.JudgeManager;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.shiro.sojbackendmodel.model.codesandbox.JudgeInfo;
import com.shiro.sojbackendmodel.model.dto.question.JudgeCase;
import com.shiro.sojbackendmodel.model.entity.Question;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Value("${codeSendBox.type}")
    private String type;

    @DubboReference(version = "1.0.0", providedBy = "question-service", providerPort = 50052, providerNamespace = "soj")
    private QuestionDubboService questionDubboService;
    @Resource
    private JudgeManager judgeManager;
    @Resource
    private CodeSendBoxFactory codeSendBoxFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionSubmit judge(Long questionSubmitId) {
        //非空校验
        QuestionSubmit questionSubmit = questionDubboService.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionDubboService.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        //更改题目提交状态为判题中
        if (!Objects.equals(questionSubmit.getStatus(), QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中,或已判题完成");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmit.getId());
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean success = questionDubboService.updateQuestionSubmitById(questionSubmitUpdate);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        //调用代码沙箱
        CodeSendBox codeSendBoxInstance = codeSendBoxFactory.getCodeSendBoxInstance(type);
        codeSendBoxInstance = new CodeSendBoxProxy(codeSendBoxInstance);
        List<JudgeCase> judgeCaseList = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSendBoxInstance.executeCode(executeCodeRequest);

        if (executeCodeResponse == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if (executeCodeResponse.getStatus() == 3) {
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            judgeInfo.setTime(0L);
            judgeInfo.setMemory(0L);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            questionDubboService.updateQuestionSubmitById(questionSubmitUpdate);
            return questionDubboService.getQuestionSubmitById(questionSubmitId);
        }
        List<String> outputList = executeCodeResponse.getOutputList();

        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setQuestion(question);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestionSubmit(questionSubmit);

        JudgeInfo judgeInfo = judgeManager.judge(judgeContext);

        //修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmit.getId());

        //修改提交状态
        if (Objects.equals(judgeInfo.getMessage(), JudgeInfoMessageEnum.ACCEPTED.getValue())) {
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());

            //修改题目的通过数
            question.setAcceptedNum(question.getAcceptedNum() + 1);
            questionDubboService.updateQuestionById(question);
        } else {
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        }

        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        success = questionDubboService.updateQuestionSubmitById(questionSubmitUpdate);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return questionDubboService.getQuestionSubmitById(questionSubmitId);
    }

}
