package com.shiro.sojbackendmodel.model.vo;

import com.shiro.sojbackendmodel.model.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 *
 * @author shiro
 **/
@Data
public class UserVO implements Serializable {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public static User toUser(UserVO userVO) {
        User user = new User();
        BeanUtils.copyProperties(userVO, user);
        return user;
    }

    @Serial
    private static final long serialVersionUID = 1L;
}