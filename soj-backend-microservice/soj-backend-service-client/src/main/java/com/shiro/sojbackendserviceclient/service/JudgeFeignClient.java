package com.shiro.sojbackendserviceclient.service;


import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 判题服务
 */
@FeignClient(name = "soj-backend-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {

    /**
     * 判题
     *
     * @param questionSubmitId 题目提交id
     * @return 判题结果
     */
    @PostMapping("/judge")
    QuestionSubmit judge(@RequestParam("questionSubmitId") Long questionSubmitId);
}
