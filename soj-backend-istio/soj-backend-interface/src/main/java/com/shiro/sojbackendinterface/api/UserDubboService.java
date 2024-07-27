package com.shiro.sojbackendinterface.api;

import com.shiro.sojbackendmodel.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Set;

public interface UserDubboService {

    Boolean isAdmin(HttpServletRequest request);

    User getById(Long userId);

    Collection<User> listByIds(Set<Long> userIdSet);
}
