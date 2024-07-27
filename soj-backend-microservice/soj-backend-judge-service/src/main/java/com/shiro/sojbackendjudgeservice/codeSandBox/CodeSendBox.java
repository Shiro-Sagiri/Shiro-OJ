package com.shiro.sojbackendjudgeservice.codeSandBox;


import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSendBox {


    /**
     * 执行代码
     * @param executeCodeRequest 请求参数
     * @return 执行结果
     */
     ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
