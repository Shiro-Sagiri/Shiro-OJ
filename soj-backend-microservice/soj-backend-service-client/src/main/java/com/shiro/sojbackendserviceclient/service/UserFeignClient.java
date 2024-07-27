package com.shiro.sojbackendserviceclient.service;


import com.shiro.sojbackendmodel.model.entity.User;
import com.shiro.sojbackendmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

@FeignClient(name = "soj-backend-user-service", path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 获取登录用户是否为管理员
     *
     * @return 是否为管理员
     */
    @GetMapping("/isAdmin")
    boolean isAdmin(HttpServletRequest request);

    /**
     * @param user 用户
     * @return 用户信息
     */
    default UserVO getUserVO(User user) {
        if (user != null) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }
        return null;
    }

    /**
     * 根据id获取用户
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") Long id);

    /**
     * 根据id列表获取用户列表
     *
     * @param idList 用户id列表
     * @return 用户列表
     */
    @GetMapping("/get/idList")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);
}
