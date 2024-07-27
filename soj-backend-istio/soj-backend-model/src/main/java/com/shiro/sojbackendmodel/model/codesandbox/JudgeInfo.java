package com.shiro.sojbackendmodel.model.codesandbox;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class JudgeInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -1522799799334211999L;
    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存(kb)
     */
    private Long memory;

    /**
     * 消耗时间(ms)
     */
    private Long time;

}
