package com.shiro.sojbackendmodel.model.codesandbox;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 进程执行信息
 */
@Data
public class ExecuteMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = -1634856134282184224L;

    private Integer exitValue;

    private String message;

    private String errorMessage;

    private Long time;

    private Long memory;


}
