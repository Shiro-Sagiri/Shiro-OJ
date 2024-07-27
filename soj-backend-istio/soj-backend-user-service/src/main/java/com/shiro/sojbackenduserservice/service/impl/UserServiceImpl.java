package com.shiro.sojbackenduserservice.service.impl;

import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiro.sojbackendcommon.constant.UserConstant;
import com.shiro.sojbackendcommon.enums.ErrorCode;
import com.shiro.sojbackendcommon.exception.BusinessException;
import com.shiro.sojbackendmodel.model.dto.user.UserLoginDTO;
import com.shiro.sojbackendmodel.model.dto.user.UserRegisterDTO;
import com.shiro.sojbackendmodel.model.dto.user.UserUpdateDTO;
import com.shiro.sojbackendmodel.model.entity.User;
import com.shiro.sojbackendmodel.model.vo.UserVO;
import com.shiro.sojbackenduserservice.mapper.UserMapper;
import com.shiro.sojbackenduserservice.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final Object lock = new Object();

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Long userRegister(UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO.getUserAccount().length() < 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于5");
        }
        if (userRegisterDTO.getUserPassword().length() < 6 || userRegisterDTO.getCheckPassword().length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于6");
        }
        if (!userRegisterDTO.getUserPassword().equals(userRegisterDTO.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserAccount, userRegisterDTO.getUserAccount());

        //检验账号是否重复
        synchronized (lock) {
            Long count = baseMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
            }
        }

        String encryptPassword = BCrypt.hashpw(userRegisterDTO.getUserPassword(), BCrypt.gensalt());
        User user = new User();
        user.setUserAccount(userRegisterDTO.getUserAccount());
        user.setUserPassword(encryptPassword);
        user.setUserName(userRegisterDTO.getUserAccount());
        user.setUserRole(UserConstant.DEFAULT_ROLE);
        boolean res = save(user);
        if (!res) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败,系统异常");
        }
        return user.getId();
    }

    @Override
    public Long updateUser(UserUpdateDTO userUpdateDTO) {
        User user = baseMapper.selectById(userUpdateDTO.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!StringUtils.isAnyBlank(userUpdateDTO.getUserPassword())) {
            //如果DTO中密码不为空,即为修改密码操作
            if (!userUpdateDTO.getUserNewPassword().equals(userUpdateDTO.getCheckPassword())) {
                throw new BusinessException(ErrorCode.ERROR_CHECK_PASSWORD);
            }
            if (!BCrypt.checkpw(userUpdateDTO.getUserPassword(), user.getUserPassword())) {
                throw new BusinessException(ErrorCode.ERROR_PASSWORD);
            }
            user.setUserPassword(BCrypt.hashpw(userUpdateDTO.getUserNewPassword(), BCrypt.gensalt()));
            baseMapper.updateById(user);
        } else {
            //如果为空则为修改其他信息
            if (StringUtils.isAllBlank(userUpdateDTO.getUserRole(), userUpdateDTO.getUserName(), userUpdateDTO.getUserProfile())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            if (!StringUtils.isAnyBlank(userUpdateDTO.getUserName())) {
                user.setUserName(userUpdateDTO.getUserName());
            }
            if (!StringUtils.isAnyBlank(userUpdateDTO.getUserProfile())) {
                user.setUserProfile(userUpdateDTO.getUserProfile());
            }
            if (!StringUtils.isAnyBlank(userUpdateDTO.getUserRole())) {
                //只有管理员才可修改用户权限
                if (!Objects.equals(user.getUserRole(), UserConstant.ADMIN_ROLE)) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
                user.setUserRole(userUpdateDTO.getUserRole());
            }
            baseMapper.updateById(user);
        }
        return userUpdateDTO.getId();
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user != null) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }
        return null;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userId = JWTUtil.parseToken(request.getHeader("Authorization")).getPayload("userId");
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        } else {
            return baseMapper.selectById((Long) userId).getUserRole().equals(UserConstant.ADMIN_ROLE);
        }
    }

    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        JWT jwt = JWTUtil.parseToken(token);
        Object userId = jwt.getPayload("userId");
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User user = this.baseMapper.selectById(Long.valueOf(String.valueOf(userId)));
        if (null == user) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public String userLogin(UserLoginDTO userLoginDTO) {
        if (userLoginDTO.getUserAccount().length() < 5 || userLoginDTO.getUserPassword().length() < 6) {
            throw new BusinessException(ErrorCode.ERROR_PASSWORD_OR_ACCOUNT);
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserAccount, userLoginDTO.getUserAccount());
        User user = this.getOne(wrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.ERROR_PASSWORD_OR_ACCOUNT);
        }
        if (!BCrypt.checkpw(userLoginDTO.getUserPassword(), user.getUserPassword())) {
            throw new BusinessException(ErrorCode.ERROR_PASSWORD_OR_ACCOUNT);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token;
        token = JWTUtil.createToken(claims, KeyUtil.generateKey("HmacSHA256").getEncoded());
        stringRedisTemplate.opsForValue().set(String.valueOf(user.getId()), token);
        log.info("token:{}", token);
        return token;
    }

}
