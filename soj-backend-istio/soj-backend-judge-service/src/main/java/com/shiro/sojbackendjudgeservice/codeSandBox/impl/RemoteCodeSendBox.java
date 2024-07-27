package com.shiro.sojbackendjudgeservice.codeSandBox.impl;

import com.shiro.sojbackendinterface.api.CodeSandboxDubboService;
import com.shiro.sojbackendjudgeservice.codeSandBox.CodeSendBox;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * 远程代码沙箱(实际调用接口的沙箱)
 */
@Slf4j
@Component
public class RemoteCodeSendBox implements CodeSendBox {

    @DubboReference(version = "1.0.0", providedBy = "code-sandbox-service", providerPort = 50052, providerNamespace = "soj")
    private CodeSandboxDubboService codeSandboxDubboService;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("远程代码沙箱执行代码");
        return codeSandboxDubboService.executeCode(executeCodeRequest);
    }
}
