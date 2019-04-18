package com.lingting.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Demo class
 *
 * @author keriezhang
 * @date 2016/10/31
 */
@Configuration
public class RabbitmqConfig {

    // 定义队列名称
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    public static final String QUEUE_INFORM_EMAIL = "queue_inform_email";

    // 定义交换机
    public static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";

    /**
     * 交换机配置，提供了 fanout、direct、topic、header交换机类型的配置
     * @return
     */
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange exchangeTopicsInform() {
        // durable(true) 持久化，消息队列重启后交换机任然存在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }

    /**
     * 声明队列 sms
     * @return
     */
    @Bean(QUEUE_INFORM_SMS)
    public Queue queueInformSms() {
        return new Queue(QUEUE_INFORM_SMS);
    }

    /**
     * 声明队列 email
     * @return
     */
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue queueInformEmail() {
        return new Queue(QUEUE_INFORM_EMAIL);
    }

    /** 绑定队列到交换机，返回值不用，这样配的原因就是让 队列和交换机产生绑定关系
     *  channel.queueBind(INFORM_QUEUE_SMS,"inform_exchange_topic","inform.#.sms.#");
     *  @Qualifier 注解表示一个bean,value表示bean的名称
     *  noargs表示不需要参数，一般也不会添加什么参数
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingQueueInformSms(
            @Qualifier(QUEUE_INFORM_SMS) Queue queue,
            @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange
    ) {
        return BindingBuilder.bind(queue).to(exchange).with("inform.#.sms.#").noargs();
    }

    @Bean
    public Binding bindingQueueInformEmail(
            @Qualifier(QUEUE_INFORM_EMAIL) Queue queue,
            @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange
    ) {
        return BindingBuilder.bind(queue).to(exchange).with("inform.#.email.#").noargs();
    }
}
