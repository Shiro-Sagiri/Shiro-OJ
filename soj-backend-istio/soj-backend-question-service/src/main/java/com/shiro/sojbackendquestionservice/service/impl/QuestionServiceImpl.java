package com.shiro.sojbackendquestionservice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiro.sojbackendcommon.enums.ErrorCode;
import com.shiro.sojbackendcommon.exception.BusinessException;
import com.shiro.sojbackendcommon.exception.ThrowUtils;
import com.shiro.sojbackendinterface.api.UserDubboService;
import com.shiro.sojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.shiro.sojbackendmodel.model.entity.Question;
import com.shiro.sojbackendmodel.model.entity.User;
import com.shiro.sojbackendmodel.model.vo.QuestionVO;
import com.shiro.sojbackendmodel.model.vo.UserVO;
import com.shiro.sojbackendquestionservice.mapper.QuestionMapper;
import com.shiro.sojbackendquestionservice.service.QuestionService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Shiro
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2023-09-05 18:50:43
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @DubboReference(version = "1.0.0", providedBy = "user-service", providerPort = 50052,providerNamespace = "soj")
    private UserDubboService userDubboService;

    /**
     * 校验题目是否合法
     *
     * @param question 题目
     * @param add      是否为添加
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeConfig = question.getJudgeConfig();
        String judgeCase = question.getJudgeCase();


        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StrUtil.hasBlank(title, content, tags, judgeCase, judgeConfig, answer), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StrUtil.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StrUtil.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StrUtil.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StrUtil.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
        if (StrUtil.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param questionQueryRequest 查询条件
     * @return 查询包装类
     */
    @Override
    public LambdaQueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        if (questionQueryRequest == null) {
            return wrapper;
        }


        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();

        // 拼接查询条件
        wrapper.like(StrUtil.isNotBlank(title), Question::getTitle, title);
        wrapper.like(StrUtil.isNotBlank(answer), Question::getAnswer, answer);
        wrapper.like(StrUtil.isNotBlank(content), Question::getContent, content);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                wrapper.like(Question::getTags, "\"" + tag + "\"");
            }
        }
        wrapper.eq(ObjectUtil.isNotEmpty(id), Question::getId, id);
        wrapper.eq(ObjectUtil.isNotEmpty(userId), Question::getUserId, userId);
        wrapper.eq(Question::getIsDelete, false);
        return wrapper;
    }

    @Override
    public QuestionVO getQuestionVO(Question question) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userDubboService.getById(userId);
        }
        UserVO userVO = User.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userDubboService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(User.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }


}




