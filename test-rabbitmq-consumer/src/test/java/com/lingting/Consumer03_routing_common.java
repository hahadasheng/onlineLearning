package com.lingting;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer03_routing_common {

    // 队列名称
    // 记录情况的队列，存放所有情况
    private static final String QUEUE_INFORM_COMMON = "queue_inform_common";

    // 交换机名称
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";

    // routingKey
    private static final String DEBUG = "debug";
    private static final String INFO = "info";
    private static final String WARN = "warn";
    private static final String ERROR = "error";

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();

        // 设置MabbitMQ所在服务器的ip和端口
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        // rabbitmq默认虚拟机名称为"/"，虚拟机相当于一个独立的mq服务
        factory.setVirtualHost("/");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);

        // 声明队列
        channel.queueDeclare(QUEUE_INFORM_COMMON, true, false, false, null);

        // 交换机和队列绑定
        channel.queueBind(QUEUE_INFORM_COMMON, EXCHANGE_ROUTING_INFORM, DEBUG);
        channel.queueBind(QUEUE_INFORM_COMMON, EXCHANGE_ROUTING_INFORM, INFO);
        channel.queueBind(QUEUE_INFORM_COMMON, EXCHANGE_ROUTING_INFORM, WARN);
        channel.queueBind(QUEUE_INFORM_COMMON, EXCHANGE_ROUTING_INFORM, ERROR);

        // 定义消费方法，重写handleDelivery方法
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                String exchange = envelope.getExchange();

                // 消息内容
                System.out.println(new String(body, "utf-8"));
            }
        };

        channel.basicConsume(QUEUE_INFORM_COMMON, true, consumer);
    }
}
