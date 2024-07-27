package com.shiro.sojbackenduserservice.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.jwt.JWTUtil;
import com.shiro.sojbackendcommon.common.Result;
import com.shiro.sojbackendcommon.enums.ErrorCode;
import com.shiro.sojbackendcommon.exception.BusinessException;
import com.shiro.sojbackendmodel.model.dto.user.UserLoginDTO;
import com.shiro.sojbackendmodel.model.dto.user.UserRegisterDTO;
import com.shiro.sojbackendmodel.model.dto.user.UserUpdateDTO;
import com.shiro.sojbackendmodel.model.entity.User;
import com.shiro.sojbackendmodel.model.vo.UserVO;
import com.shiro.sojbackenduserservice.manager.MinioManager;
import com.shiro.sojbackenduserservice.service.UserService;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private MinioManager minioManager;

    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO == null || StringUtils.isAnyBlank(userRegisterDTO.getUserAccount(), userRegisterDTO.getUserPassword(), userRegisterDTO.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Long userId = userService.userRegister(userRegisterDTO);
        return Result.success(userId).setMessage("注册成功");
    }

    @PostMapping("/login")
    public Result<String> userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        if (userLoginDTO == null || StringUtils.isAnyBlank(userLoginDTO.getUserAccount(), userLoginDTO.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String token = userService.userLogin(userLoginDTO);
        return Result.success(token).setMessage("登录成功");
    }

    @GetMapping("/getLoginUser")
    public Result<UserVO> getLoginUser(HttpServletRequest request) {
        UserVO userVO = userService.getLoginUser(request);
        return Result.success(userVO).setMessage("获取成功");
    }

    /**
     * 用户信息更新
     *
     * @param userUpdateDTO 用户信息更新DTO
     * @return 用户视图对象
     */
    @PutMapping("/updateInfo")
    public Result<Long> updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Object userId = JWTUtil.parseToken(token).getPayload("userId");
        Long id = Long.valueOf(String.valueOf(userId));
        if (userUpdateDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userUpdateDTO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!userUpdateDTO.getId().equals(id)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        id = userService.updateUser(userUpdateDTO);
        return Result.success(id).setMessage("账号信息更新成功");
    }

    @PostMapping("/updateAvatar")
    public Result<String> updateUserAvatar(@RequestBody MultipartFile file, HttpServletRequest request) {
        //将MultipartFile转换成File
        File uploadFile;
        try {
            String originalFilename = file.getOriginalFilename();
            String[] filename = new String[0];
            if (originalFilename != null) {
                filename = originalFilename.split("\\.");
            }
            uploadFile = File.createTempFile(filename[0], filename[1]);
            file.transferTo(uploadFile);
            uploadFile.deleteOnExit();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        // 创建PutObjectRequest对象:参数 bucket 和 文件名 和 文件对象
        String filename = UUID.randomUUID() + file.getOriginalFilename();

        // 上传文件。
        String url;
        try {
            url = minioManager.uploadFile(minioManager.getFileInputStream(filename), filename);
        } catch (ServerException | InsufficientDataException |
                 ErrorResponseException | IOException | NoSuchAlgorithmException | InvalidKeyException |
                 InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }

        //修改用户头像url
        User user = userService.getById(request.getHeader("userId"));
        user.setUserAvatar(url);
        userService.updateById(user);

        return Result.success(url).setMessage("修改头像成功");
    }
}