package com.shiro.sojbackendquestionservice.service.impl;

import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiro.sojbackendcommon.enums.ErrorCode;
import com.shiro.sojbackendcommon.enums.QuestionLanguageEnum;
import com.shiro.sojbackendcommon.enums.QuestionSubmitStatusEnum;
import com.shiro.sojbackendcommon.exception.BusinessException;
import com.shiro.sojbackendmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.shiro.sojbackendmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.shiro.sojbackendmodel.model.entity.Question;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import com.shiro.sojbackendmodel.model.vo.QuestionSubmitVO;
import com.shiro.sojbackendmodel.model.vo.QuestionVO;
import com.shiro.sojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.shiro.sojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.shiro.sojbackendquestionservice.service.QuestionService;
import com.shiro.sojbackendquestionservice.service.QuestionSubmitService;
import com.shiro.sojbackendserviceclient.service.JudgeFeignClient;
import com.shiro.sojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Shiro
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-09-05 18:51:53
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private QuestionSubmitMapper questionSubmitMapper;
    @Resource
    private MyMessageProducer myMessageProducer;

    @Override
    public Long questionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        Object userId = JWTUtil.parseToken(request.getHeader("Authorization")).getPayload("userId");
        Question question = questionService.getById(questionSubmitAddRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        //判断编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionLanguageEnum languageEnum = QuestionLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不合法");
        }
        questionSubmit.setLanguage(language);
        //设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setUserId(Long.valueOf(String.valueOf(userId)));
        boolean res = this.save(questionSubmit);
        if (!res) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交失败");
        }
        Long questionSubmitId = questionSubmit.getId();

        //异步判题,将判题任务放入消息队列
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));

        //更新题目提交次数
        question.setSubmitNum(question.getSubmitNum() + 1);
        questionService.updateById(question);

        return questionSubmitId;
    }

    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest 查询条件
     * @return 查询包装类
     */
    @Override
    public LambdaQueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        LambdaQueryWrapper<QuestionSubmit> wrapper = new LambdaQueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return wrapper;
        }

        // 1. 获取查询条件
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();

        // 拼接查询条件
        wrapper.eq(StringUtils.isNotBlank(language), QuestionSubmit::getLanguage, language);
        wrapper.eq(ObjectUtils.isNotEmpty(questionId), QuestionSubmit::getUserId, questionId);
        wrapper.eq(ObjectUtils.isNotEmpty(userId), QuestionSubmit::getUserId, userId);
        wrapper.eq(QuestionSubmitStatusEnum.getByValue(status) != null, QuestionSubmit::getStatus, status);
        wrapper.eq(QuestionSubmit::getIsDelete, false);
        return wrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, HttpServletRequest request) {
        Object userId = JWTUtil.parseToken(request.getHeader("Authorization")).getPayload("userId");
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        //脱敏(仅管理员和本人可见)
        if (!Long.valueOf(String.valueOf(userId)).equals(questionSubmit.getUserId()) && !userFeignClient.isAdmin(request)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmit> pageQuery(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        List<QuestionSubmit> questionSubmits = questionSubmitMapper.pageQuery(questionSubmitQueryRequest);
        Long total = questionSubmitMapper.getTotal(questionSubmitQueryRequest);
        Page<QuestionSubmit> questionSubmitPages = new Page<>(questionSubmitQueryRequest.getCurrent(), questionSubmitQueryRequest.getPageSize(), total);
        questionSubmitPages.setRecords(questionSubmits);
        return questionSubmitPages;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> submitVOList = questionSubmitList.stream().map(item -> {
            QuestionSubmitVO questionSubmitVO = this.getQuestionSubmitVO(item, request);
            Long questionId = questionSubmitVO.getQuestionId();
            QuestionVO questionVO = questionService.getQuestionVO(questionService.getById(questionId));
            questionSubmitVO.setQuestionVO(questionVO);
            return questionSubmitVO;
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(submitVOList);
        return questionSubmitVOPage;
    }
}




