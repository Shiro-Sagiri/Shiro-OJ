package com.shiro.sojbackenduserservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.shiro.sojbackendmodel.model.dto.user.UserLoginDTO;
import com.shiro.sojbackendmodel.model.dto.user.UserRegisterDTO;
import com.shiro.sojbackendmodel.model.dto.user.UserUpdateDTO;
import com.shiro.sojbackendmodel.model.entity.User;
import com.shiro.sojbackendmodel.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册信息
     * @return 用户id
     */
    Long userRegister(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录
     *
     * @param userLoginDTO 用户登录信息
     * @return 用户信息
     */
    String userLogin(UserLoginDTO userLoginDTO);

    /**
     * 获取登录用户信息
     *
     * @return 用户信息
     */
    UserVO getLoginUser(HttpServletRequest request);

    /**
     * 获取登录用户是否为管理员
     *
     * @return 是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * @param user 用户
     * @return 用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 用户信息更新
     *
     * @param userUpdateDTO 用户信息更新DTO
     * @return 用户视图对象
     */
    Long updateUser(UserUpdateDTO userUpdateDTO);
}
