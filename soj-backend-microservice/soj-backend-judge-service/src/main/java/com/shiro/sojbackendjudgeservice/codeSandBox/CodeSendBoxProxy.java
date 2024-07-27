package com.shiro.sojbackendjudgeservice.codeSandBox;

import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱代理
 */
@Slf4j
public class CodeSendBoxProxy implements CodeSendBox{

    private final CodeSendBox codeSendBox;

    public CodeSendBoxProxy(CodeSendBox codeSendBox){
        this.codeSendBox = codeSendBox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("请求参数:{}",executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSendBox.executeCode(executeCodeRequest);
        log.info("响应参数:{}",executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
