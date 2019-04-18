package com.lingting.mq;

import com.lingting.config.RabbitmqConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 监听队列
 */
@Component
public class ReceiverHandler {

    /**
     * 监听 sms 队列
     * @param msg
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_SMS})
    public void monitorSms(String msg, Message message, Channel channel) {
        System.out.println("sms: " + msg);
    }

    /**
     * 监听 email队列
     * @param msg
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void monitorEmail(String msg, Message message, Channel channel) {
        System.out.println("email: " + msg);
    }




}
