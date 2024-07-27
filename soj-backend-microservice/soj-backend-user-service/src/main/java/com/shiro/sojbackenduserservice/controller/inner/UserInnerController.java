package com.shiro.sojbackenduserservice.controller.inner;

import com.shiro.sojbackendmodel.model.entity.User;
import com.shiro.sojbackendserviceclient.service.UserFeignClient;
import com.shiro.sojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

/**
 * 用户内部接口, 用于Feign调用, 不对外暴露
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    /**
     * 获取登录用户是否为管理员
     *
     * @return 是否为管理员
     */
    @Override
    @GetMapping("/isAdmin")
    public boolean isAdmin(HttpServletRequest request) {
        return userService.isAdmin(request);
    }

    /**
     * 根据id获取用户
     *
     * @param id 用户id
     * @return 用户
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") Long id) {
        return userService.getById(id);
    }

    /**
     * 根据id列表获取用户列表
     *
     * @param idList 用户id列表
     * @return 用户列表
     */
    @Override
    @GetMapping("/get/idList")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }
}
