package com.shiro.sojbackendjudgeservice.codeSandBox.impl;

import com.shiro.sojbackendjudgeservice.codeSandBox.CodeSendBox;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 第三方代码沙箱(调用现成的代码沙箱)
 */
@Slf4j
public class ThirdPartyCodeSendBox implements CodeSendBox {


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("第三方代码沙箱执行代码");
        return null;
    }
}
