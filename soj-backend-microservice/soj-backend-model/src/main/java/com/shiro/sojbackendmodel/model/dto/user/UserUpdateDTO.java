package com.shiro.sojbackendmodel.model.dto.user;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 6218532766442332012L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 新密码
     */
    private String userNewPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

}
