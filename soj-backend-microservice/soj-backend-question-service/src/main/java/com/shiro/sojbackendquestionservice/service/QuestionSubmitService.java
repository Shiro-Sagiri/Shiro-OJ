package com.shiro.sojbackendquestionservice.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shiro.sojbackendmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.shiro.sojbackendmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import com.shiro.sojbackendmodel.model.vo.QuestionSubmitVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Shiro
 * @description 针对表【question_submit(题目提交)】的数据库操作Service
 * @createDate 2023-09-05 18:51:53
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {


    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 添加问题DTO
     * @return 提交记录的id
     */
    Long questionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest 查询条件
     * @return 查询条件构造器
     */
    LambdaQueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取问题封装
     *
     * @param questionSubmit 问题提交
     * @return 问题提交封装
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, HttpServletRequest request);

    /**
     * 分页获取问题封装
     *
     * @param questionSubmitPage 问题提交分页
     * @return 问题提交封装分页
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request);

    /**
     * 分页查询
     *
     * @param questionSubmitQueryRequest 查询条件
     * @return QuestionSubmit
     */
    Page<QuestionSubmit> pageQuery(QuestionSubmitQueryRequest questionSubmitQueryRequest);
}
