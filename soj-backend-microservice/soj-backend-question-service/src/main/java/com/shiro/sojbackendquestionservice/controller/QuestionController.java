package com.shiro.sojbackendquestionservice.controller;

import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nimbusds.jose.shaded.gson.Gson;
import com.shiro.sojbackendcommon.annotation.AuthCheck;
import com.shiro.sojbackendcommon.common.DeleteRequest;
import com.shiro.sojbackendcommon.common.Result;
import com.shiro.sojbackendcommon.constant.UserConstant;
import com.shiro.sojbackendcommon.enums.ErrorCode;
import com.shiro.sojbackendcommon.exception.BusinessException;
import com.shiro.sojbackendcommon.exception.ThrowUtils;
import com.shiro.sojbackendmodel.model.dto.question.*;
import com.shiro.sojbackendmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.shiro.sojbackendmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.shiro.sojbackendmodel.model.entity.Question;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import com.shiro.sojbackendmodel.model.vo.QuestionSubmitVO;
import com.shiro.sojbackendmodel.model.vo.QuestionVO;
import com.shiro.sojbackendquestionservice.service.QuestionService;
import com.shiro.sojbackendquestionservice.service.QuestionSubmitService;
import com.shiro.sojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 问题接口
 *
 * @author Shiro
 */
@RestController
@RequestMapping
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitService questionSubmitService;

    private final static Gson GSON = new Gson();

    //region

    /**
     * 创建(管理员)
     *
     * @param questionAddRequest 添加问题DTO
     * @return Result<Long>
     */
    @PostMapping("/add")
    public Result<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        Object userId = JWTUtil.parseToken(request.getHeader("Authorization")).getPayload("userId");
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //封装参数
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        question.setJudgeConfig(JSONUtil.toJsonStr(questionAddRequest.getJudgeConfig()));
        List<JudgeCase> judgeCaseList = questionAddRequest.getJudgeCase();
        List<String> stringList = judgeCaseList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        question.setJudgeCase(stringList.toString());
        question.setUserId(Long.valueOf(String.valueOf(userId)));
        questionService.validQuestion(question, true);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(question.getId()).setMessage("问题创建成功");
    }

    /**
     * 删除(管理员)
     *
     * @param deleteRequest 删除请求DTO
     * @return Result<Boolean>
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        Object userId = JWTUtil.parseToken(request.getHeader("Authorization")).getPayload("userId");
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR, "问题不存在");
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(Long.valueOf(String.valueOf(userId))) && !userFeignClient.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return Result.success(b).setMessage("问题删除成功");
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest 更新问题DTO
     * @return Result<Boolean>
     */
    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //封装参数
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        if (questionUpdateRequest.getJudgeConfig() != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(questionUpdateRequest.getJudgeConfig()));
        }
        List<JudgeCase> judgeCaseList = questionUpdateRequest.getJudgeCase();
        if (judgeCaseList != null) {
            List<String> stringList = judgeCaseList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
            question.setJudgeCase(stringList.toString());
        }

        // 参数校验
        questionService.validQuestion(question, false);
        Long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return Result.success(result).setMessage("问题更新成功");
    }

    /**
     * 根据 id 获取(脱敏)
     *
     * @param id id
     * @return Result<QuestionVO>
     */
    @GetMapping("/getQuestionVO")
    public Result<QuestionVO> getQuestionVOById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return Result.success(questionService.getQuestionVO(question)).setMessage("获取问题成功");
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return Result<Question>
     */
    @GetMapping("/getQuestion")
    public Result<Question> getQuestionById(Long id, HttpServletRequest request) {
        Object userId = JWTUtil.parseToken(request.getHeader("Authorization")).getPayload("userId");
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!question.getUserId().equals(Long.valueOf(String.valueOf(userId))) && !userFeignClient.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return Result.success(question).setMessage("获取问题成功");
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest 查询问题DTO
     * @return Result<Page < QuestionVO>>
     */
    @PostMapping("/list")
    public Result<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        LambdaQueryWrapper<Question> wrapper = questionService.getQueryWrapper(questionQueryRequest);
        wrapper.orderByDesc(Question::getUpdateTime);
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> QuestionPage = questionService.page(new Page<>(current, size), wrapper
        );
        return Result.success(questionService.getQuestionVOPage(QuestionPage)).setMessage("获取问题列表成功");
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param QuestionQueryRequest 查询问题DTO
     * @return Result<Page < QuestionVO>>
     */
    @PostMapping("/my/list")
    public Result<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest QuestionQueryRequest, HttpServletRequest request) {
        Object userId = JWTUtil.parseToken(request.getHeader("Authorization")).getPayload("userId");
        if (QuestionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionQueryRequest.setUserId(Long.valueOf(String.valueOf(userId)));
        long current = QuestionQueryRequest.getCurrent();
        long size = QuestionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> QuestionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(QuestionQueryRequest));
        return Result.success(questionService.getQuestionVOPage(QuestionPage)).setMessage("分页获取问题列表成功");
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param QuestionEditRequest 编辑问题DTO
     * @return Result<Boolean>
     */
    @PostMapping("/edit")
    public Result<Boolean> editQuestion(@RequestBody QuestionEditRequest QuestionEditRequest, HttpServletRequest request) {
        Object userId = JWTUtil.parseToken(request.getHeader("Authorization")).getPayload("userId");
        if (QuestionEditRequest == null || QuestionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(QuestionEditRequest, question);
        // 参数校验
        questionService.validQuestion(question, false);
        Long id = QuestionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可编辑
        if (!oldQuestion.getUserId().equals(Long.valueOf(String.valueOf(userId)))) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return Result.success(result).setMessage("问题编辑成功").setMessage("问题编辑成功");
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/getQuestionPage")
    public Result<Page<Question>> getQuestionPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        LambdaQueryWrapper<Question> wrapper = questionService.getQueryWrapper(questionQueryRequest);
        wrapper.orderByDesc(Question::getUpdateTime);
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> QuestionPage = questionService.page(new Page<>(current, size), wrapper
        );
        return Result.success(QuestionPage).setMessage("获取问题列表成功");
    }

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 添加问题DTO
     * @return 提交记录的id
     */
    @PostMapping("/questionSubmit/submit")
    public Result<Long> questionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long questionSubmitId = questionSubmitService.questionSubmit(questionSubmitAddRequest, request);
        return Result.success(questionSubmitId).setMessage("提交成功");
    }


    /**
     * 分页获取提交记录
     * 普通用户只能查到非代码的公开信息
     *
     * @param questionSubmitQueryRequest 查询条件
     * @return 分页的提交记录
     */
    @PostMapping("/questionSubmit/pageList")
    public Result<Page<QuestionSubmitVO>> getQuestionSubmitPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (questionSubmitQueryRequest.getTags() != null) {
            if (questionSubmitQueryRequest.getTags().isEmpty()) {
                questionSubmitQueryRequest.setTags(null);
            }
        }
        if (StringUtils.isAnyBlank(questionSubmitQueryRequest.getTitle())) {
            questionSubmitQueryRequest.setTitle(null);
        }
        if (StringUtils.isAnyBlank(questionSubmitQueryRequest.getLanguage())) {
            questionSubmitQueryRequest.setLanguage(null);
        }
        //从数据库中查询原始信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.pageQuery(questionSubmitQueryRequest);
        return Result.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, request));
    }
}
