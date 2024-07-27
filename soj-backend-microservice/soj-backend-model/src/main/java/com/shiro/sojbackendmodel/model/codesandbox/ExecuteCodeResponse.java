package com.shiro.sojbackendmodel.model.codesandbox;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {


    /**
     * 输出用例
     */
    private List<String> outputList;

    /**
     * 接口信息
     */
    private String message;

    /**
     *状态
     */
    private Integer status;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
