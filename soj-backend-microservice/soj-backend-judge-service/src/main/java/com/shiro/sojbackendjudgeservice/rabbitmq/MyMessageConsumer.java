package com.shiro.sojbackendjudgeservice.rabbitmq;


import com.rabbitmq.client.Channel;
import com.shiro.sojbackendjudgeservice.service.JudgeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    JudgeService judgeService;

    /**
     * 程序指定监听的消息队列和确认机制
     *
     * @param message 消息内容
     */
    @SneakyThrows
    @RabbitListener(queues = "code_queue", ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receive message: {}", message);
        try {
            Long questionSubmitId = Long.parseLong(message);
            judgeService.judge(questionSubmitId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("judge error: {}", e.getMessage());
            channel.basicNack(deliveryTag, false,false);
        }
    }
}
