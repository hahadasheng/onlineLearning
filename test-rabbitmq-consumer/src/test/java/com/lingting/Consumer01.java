package com.lingting;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer01 {

    // 队列名称
    private static final String QUEUE = "helloworld";

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();

        // 设置MabbitMQ所在服务器的ip和端口
        factory.setHost("127.0.0.1");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明队列
        channel.queueDeclare(QUEUE, true, false, false, null);

        // 定义消费方法，重写handleDelivery方法
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            /**
             * 消费者接受消息调用此方法
             * @param consumerTag 消费者标签， 在channel.basicConsume()去指定
             * @param envelope 消息包的内容，可以从中获取消息id,消息routingkey,交换机，消息和重传标志(收到消息失败后是否需要重新发送)
             * @param properties 消息属性【几乎不用】
             * @param body 消息体
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 获取交换机
                String exchange = envelope.getExchange();

                // 路由key
                String routingKey = envelope.getRoutingKey();

                // 消息id
                long deliveryTag = envelope.getDeliveryTag();

                // 消息内容
                String msg = new String(body, "utf-8");
                System.out.println("receive.. " + msg);
            }
        };

        /*
         监听队列：参数明细
            queue: 队列名称
            autoAck: 是否自动回复，设置为true为表示纤细接收到自定性mq回复接收到了，mq接收到回复消息后删除消息，设置为false则需要手动回复
            callback: 消费消息的方法，消费者接受到消息后调用此方法
         */
        channel.basicConsume(QUEUE, true, consumer);
    }

}
