package com.lingting;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer04_topics_alien {

    // 队列名称

    // 三体人队列
    private static final String QUEUE_INFORM_ALIEN = "queue_inform_alien";

    // 交换机名称
    private static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";


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
        channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);

        // 声明队列
        channel.queueDeclare(QUEUE_INFORM_ALIEN, true, false, false, null);

        // 交换机和队列绑定
        /*
           每个单词以“.”分隔；“#”符号可以匹配0-n个单词；“*”符号可以匹配1个单词
        */
        channel.queueBind(QUEUE_INFORM_ALIEN, EXCHANGE_TOPICS_INFORM, "inform.#.alien.#");

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

        channel.basicConsume(QUEUE_INFORM_ALIEN, true, consumer);
    }
}
