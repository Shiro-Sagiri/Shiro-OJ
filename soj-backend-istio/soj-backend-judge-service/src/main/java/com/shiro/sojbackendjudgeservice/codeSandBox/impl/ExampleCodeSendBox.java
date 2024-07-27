package com.shiro.sojbackendjudgeservice.codeSandBox.impl;


import com.shiro.sojbackendcommon.enums.JudgeInfoMessageEnum;
import com.shiro.sojbackendcommon.enums.QuestionSubmitStatusEnum;
import com.shiro.sojbackendjudgeservice.codeSandBox.CodeSendBox;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.shiro.sojbackendmodel.model.codesandbox.JudgeInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 示例代码沙箱(仅为跑通代码流程)
 */
@Slf4j
public class ExampleCodeSendBox implements CodeSendBox {


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
