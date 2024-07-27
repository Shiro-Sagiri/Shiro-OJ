package com.shiro.sojbackendserviceclient.service;


import com.shiro.sojbackendmodel.model.entity.Question;
import com.shiro.sojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author Shiro
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2023-09-05 18:50:43
 */
@FeignClient(name = "soj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {

    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") Long questionId);

    @GetMapping("/getSubmit/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId);

    @PutMapping("/updateQuestion")
    boolean updateQuestionById(@RequestBody Question question);

    @PutMapping("/updateSubmit")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);
}
