package com.shiro.sojbackendjudgeservice.codeSandBox;


import com.shiro.sojbackendjudgeservice.codeSandBox.impl.ExampleCodeSendBox;
import com.shiro.sojbackendjudgeservice.codeSandBox.impl.RemoteCodeSendBox;
import com.shiro.sojbackendjudgeservice.codeSandBox.impl.ThirdPartyCodeSendBox;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 代码沙箱工厂(根据字符串创建对应的代码沙箱)
 */
@Component
public class CodeSendBoxFactory {

    @Resource
    private RemoteCodeSendBox remoteCodeSendBox;

    public CodeSendBox getCodeSendBoxInstance(String codeSendBoxType) {
        //默认返回示例代码沙箱
        return switch (codeSendBoxType) {
            case "remote" -> remoteCodeSendBox;
            case "thirdParty" -> new ThirdPartyCodeSendBox();
            default -> new ExampleCodeSendBox();
        };
    }
}
