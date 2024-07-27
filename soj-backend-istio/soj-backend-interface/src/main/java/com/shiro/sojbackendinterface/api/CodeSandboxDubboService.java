package com.shiro.sojbackendinterface.api;


import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;

public interface CodeSandboxDubboService {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
