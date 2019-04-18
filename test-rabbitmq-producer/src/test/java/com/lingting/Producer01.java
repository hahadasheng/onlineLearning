package com.lingting;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 点对点模式
 */
public class Producer01 {

    // 队列名称
    private static final String QUEUE = "helloworld";

    public static void main(String[] args) throws Exception {

        Connection connection = null;
        Channel channel = null;

        try {
            ConnectionFactory factory = new ConnectionFactory();

            factory.setHost("localhost");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            // rabbitmq默认虚拟机名称为"/"，虚拟机相当于一个独立的mq服务
            factory.setVirtualHost("/");

            // 创建与RabbitMQ服务的TCP连接
            connection = factory.newConnection();

            // 创建与Exchange的通道，每个连接可以常见多个通道，每个通道代表一个会话任务
            channel = connection.createChannel();

            /* 声明队列，如果Rabbit中没有此队列将自动创建
            签名如下：
                queue: 队列名称
                durable: 是否持久化，如果持久化，mq重启后消息还在
                exclusive: 队列是否独占此链接，队列只允许在该连接中访问，connection关闭队列自动删除，设置为true可用于临时队列的创建
                autoDelete: 队列不再使用时是否自动删除此队列，当和exclusive同时为true，可以作为一个临时队列(队列不再使用就自动删除)
                arguments: 参数，设置扩展参数，比如，存活时间
            */
            channel.queueDeclare(QUEUE, true, false, false, null);
            String message = "Hello World";

            /* 消息发布方法，参数签名
                exchange: 交换机，如果不指定将使用mq默认交换机(设置为"")
                          每个队列将会绑定到默认的交换机，但是不能显示绑定或解除绑定
                routingKey: 路由key, 交换机路由由key来将消息转发到指定的队列，如果使用默认交换机，routingKey设置为队列的名称
                props: 消息的属性【几乎不用】
                body: 消息内容
            */
            channel.basicPublish("", QUEUE, null, message.getBytes());
            System.out.println("消息已经发送！");

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
