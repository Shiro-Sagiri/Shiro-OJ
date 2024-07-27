package com.shiro.sojcodesandbox.provider;

import com.shiro.sojbackendinterface.api.CodeSandboxDubboService;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.shiro.sojcodesandbox.JavaNativeCodeSandbox;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public class CodeSandboxDubboServiceImpl implements CodeSandboxDubboService {

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return javaNativeCodeSandbox.executeCode(executeCodeRequest);
    }
}
