package com.shiro.sojbackendjudgeservice.codeSandBox;


import com.shiro.sojbackendjudgeservice.codeSandBox.impl.ExampleCodeSendBox;
import com.shiro.sojbackendjudgeservice.codeSandBox.impl.RemoteCodeSendBox;
import com.shiro.sojbackendjudgeservice.codeSandBox.impl.ThirdPartyCodeSendBox;

/**
 * 代码沙箱工厂(根据字符串创建对应的代码沙箱)
 */
public class CodeSendBoxFactory {

    public static CodeSendBox getCodeSendBoxInstance(String codeSendBoxType) {
        //默认返回示例代码沙箱
        switch (codeSendBoxType) {
            case "remote":
                return new RemoteCodeSendBox();
            case "thirdParty":
                return new ThirdPartyCodeSendBox();
            case "example":
            default:
                return new ExampleCodeSendBox();
        }
    }
}
