package com.shiro.sojbackendjudgeservice.codeSandBox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.shiro.sojbackendcommon.enums.ErrorCode;
import com.shiro.sojbackendcommon.exception.BusinessException;
import com.shiro.sojbackendjudgeservice.codeSandBox.CodeSendBox;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.shiro.sojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱(实际调用接口的沙箱)
 */
@Slf4j
public class RemoteCodeSendBox implements CodeSendBox {

    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("远程代码沙箱执行代码");
        String url = "http://shiro.work:8090/executeCode";
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(JSONUtil.toJsonStr(executeCodeRequest))
                .execute().body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "远程代码沙箱执行代码失败");
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
