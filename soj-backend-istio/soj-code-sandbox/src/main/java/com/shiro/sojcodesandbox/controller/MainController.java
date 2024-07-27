package com.shiro.sojcodesandbox.controller;

import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.shiro.sojcodesandbox.JavaNativeCodeSandbox;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    /**
     * 执行代码
     */
    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request) {
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        if (request.getHeader(AUTH_REQUEST_HEADER) == null || !request.getHeader(AUTH_REQUEST_HEADER).equals(AUTH_REQUEST_SECRET)) {
            throw new RuntimeException("鉴权失败");
        }
        return javaNativeCodeSandbox.executeCode(executeCodeRequest);
    }
}
