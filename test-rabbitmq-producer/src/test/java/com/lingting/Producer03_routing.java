package com.lingting;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer03_routing {

    // 队列名称
    // 记录情况的队列，存放所有情况
    private static final String QUEUE_INFORM_COMMON = "queue_inform_common";

    // 紧急情况处理的队列，只存放紧急情况
    private static final String QUEUE_INFORM_EMERGENCY = "queue_inform_emergency";

    // 交换机名称
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";

    // routingKey
    private static final String DEBUG = "debug";
    private static final String INFO = "info";
    private static final String WARN = "warn";
    private static final String ERROR = "error";
    
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
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);

            /* 声明队列，如果Rabbit中没有此队列将自动创建
            签名如下：
                queue: 队列名称
                durable: 是否持久化，如果持久化，mq重启后消息还在
                exclusive: 队列是否独占此链接，队列只允许在该连接中访问，connection关闭队列自动删除，设置为true可用于临时队列的创建
                autoDelete: 队列不再使用时是否自动删除此队列，当和exclusive同时为true，可以作为一个临时队列(队列不再使用就自动删除)
                arguments: 参数，设置扩展参数，比如，存活时间
            */
            channel.queueDeclare(QUEUE_INFORM_COMMON, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_EMERGENCY, true, false, false, null);

            /* 交换机和队列绑定，参数签名
                queue: 队列名称
                exchange: 交换机名称
                routingKey: 路由key,作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中协调为空字符串
            */
            channel.queueBind(QUEUE_INFORM_COMMON, EXCHANGE_ROUTING_INFORM, DEBUG);
            channel.queueBind(QUEUE_INFORM_COMMON, EXCHANGE_ROUTING_INFORM, INFO);
            channel.queueBind(QUEUE_INFORM_COMMON, EXCHANGE_ROUTING_INFORM, WARN);
            channel.queueBind(QUEUE_INFORM_COMMON, EXCHANGE_ROUTING_INFORM, ERROR);
            channel.queueBind(QUEUE_INFORM_EMERGENCY, EXCHANGE_ROUTING_INFORM, ERROR);
            
            /* 向交换机发送消息, 参数列表
                exchange 交换机名，不指定则使用默认交换机名称
                routingKey 根据keu名称将消息转发到具体的队列，
                props 消息属性【几乎不用】
                body 消息体 byte数组
            */
            // 发送消息，级别分别为为DEBUG INFO WARN ERROR
            channel.basicPublish(EXCHANGE_ROUTING_INFORM, DEBUG, null, "~~ :debug".getBytes());
            channel.basicPublish(EXCHANGE_ROUTING_INFORM, INFO, null, "~~ :info".getBytes());
            channel.basicPublish(EXCHANGE_ROUTING_INFORM, WARN, null, "~~ :warn".getBytes());
            channel.basicPublish(EXCHANGE_ROUTING_INFORM, ERROR, null, "~~ :error".getBytes());

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
