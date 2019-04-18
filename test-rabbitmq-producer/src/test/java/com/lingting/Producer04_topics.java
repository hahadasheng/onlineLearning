package com.lingting;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer04_topics {

    // 队列名称
    // 地球人队列
    private static final String QUEUE_INFORM_TERRAN = "queue_inform_terran";

    // 三体人队列
    private static final String QUEUE_INFORM_ALIEN = "queue_inform_alien";

    // 交换机名称
    private static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";
    
    public static void main(String[] args) throws Exception {

        Connection connection = null;
        Channel channel = null;

        try {
            // 创建一个MQ的连接
            ConnectionFactory factory = new ConnectionFactory();

            factory.setHost("127.0.0.1");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            // rabbitmq默认虚拟机名称为"/"，虚拟机相当于一个独立的mq服务
            factory.setVirtualHost("/");

            // 创建与RabbitMQ服务的TCP连接
            connection = factory.newConnection();

            // 创建与Exchange的通道，每个连接可以常见多个通道，每个通道代表一个会话任务
            channel = connection.createChannel();

            // 声明交换机，指定为路由模式
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);

            /* 声明队列，如果Rabbit中没有此队列将自动创建
            签名如下：
                queue: 队列名称
                durable: 是否持久化，如果持久化，mq重启后消息还在
                exclusive: 队列是否独占此链接，队列只允许在该连接中访问，connection关闭队列自动删除，设置为true可用于临时队列的创建
                autoDelete: 队列不再使用时是否自动删除此队列，当和exclusive同时为true，可以作为一个临时队列(队列不再使用就自动删除)
                arguments: 参数，设置扩展参数，比如，存活时间
            */
            channel.queueDeclare(QUEUE_INFORM_TERRAN, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_ALIEN, true, false, false, null);
            
            /* 向交换机发送消息, 参数列表
                exchange 交换机名，不指定则使用默认交换机名称
                routingKey 根据keu名称将消息转发到具体的队列，
                props 消息属性【几乎不用】
                body 消息体 byte数组
            */
            // 发送消息, 给地球人发
            channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.terran", null, "~~ :一群虫子".getBytes());
            //channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.xx.terran", null, "~~ :一群虫子".getBytes());
            // 给三体人发
            channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.alien", null, "~~ :不是一般的虫子".getBytes());
            // 给宇宙人发
            channel.basicPublish(EXCHANGE_TOPICS_INFORM, "inform.terran.alien", null, "~~ :别搞个人小宇宙，交出物质，我们来重启宇宙大爆炸，回归到11维的田园时代！".getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
