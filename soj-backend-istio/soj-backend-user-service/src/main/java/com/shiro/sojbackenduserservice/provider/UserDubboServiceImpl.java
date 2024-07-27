package com.shiro.sojbackenduserservice.provider;

import com.shiro.sojbackendinterface.api.UserDubboService;
import com.shiro.sojbackendmodel.model.entity.User;
import com.shiro.sojbackenduserservice.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.Collection;
import java.util.Set;

@DubboService(version = "1.0.0")
public class UserDubboServiceImpl implements UserDubboService {

    @Resource
    private UserService userService;

    @Override
    public Boolean isAdmin(HttpServletRequest request) {
        return userService.isAdmin(request);
    }

    @Override
    public User getById(Long userId) {
        return userService.getById(userId);
    }

    @Override
    public Collection<User> listByIds(Set<Long> userIdSet) {
        return userService.listByIds(userIdSet);
    }
}
