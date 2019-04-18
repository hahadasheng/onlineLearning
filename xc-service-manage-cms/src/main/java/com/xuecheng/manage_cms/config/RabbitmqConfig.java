package com.xuecheng.manage_cms.config;

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
     * 定义交换机的名称
     */
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

    /**
     * 交换机配置，提供了 direct ，即路由模式
     * @return
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange exRoutingCmsPostpage() {
        // durable(true) 持久化，消息队列重启后交换机任然存在
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

}
