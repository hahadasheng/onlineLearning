package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * 定义队列bean的名称
     */
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";

    /**
     * 定义交换机的名称
     * */
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

    // 注入队列的名称
    @Value("${xuecheng.mq.queue}")
    public String queueCmsPostPageName;

    // routingKey 及站点id
    @Value("${xuecheng.mq.routingKey}")
    public String routingKey;

    /**
     * 交换机配置，提供了 direct ，即路由模式
     * @return
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange exRoutingCmsPostpage() {
        // durable(true) 持久化，消息队列重启后交换机任然存在
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

    /**
     * 声明队列 cms
     * @return
     */
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue queueCmsPostpage() {
        return new Queue(queueCmsPostPageName);
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
    public Binding bindingQueueExchange(
            @Qualifier(QUEUE_CMS_POSTPAGE) Queue queue,
            @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange
    ) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }
}
