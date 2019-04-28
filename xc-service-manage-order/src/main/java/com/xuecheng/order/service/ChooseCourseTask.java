package com.xuecheng.order.service;

import com.rabbitmq.client.Channel;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 定时任务调度测试类
 * @author Liucheng
 * @date 2019/4/27 16:47
 */
@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    private TaskService taskService;

    /**
     * 每隔1分钟扫描消息表，向mq发送消息
     */
    //@Scheduled(fixedDelay = 60000)
    @Scheduled(fixedDelay = 2000)
    public void sendChooseCourseTask () {

        // 取出当前时间1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();

        List<XcTask> taskList = taskService.findTaskList(time, 1000);

        // 遍历任务列表
        for(XcTask xcTask:taskList) {

            // 【乐观锁校验】

            // 任务id
            String taskId = xcTask.getId();

            // 版本号
            Integer version = xcTask.getVersion();

            // 调用乐观锁校验任务是否可执行
            if (taskService.getTask(taskId, version) > 0) {
                // 发送选课消息
                taskService.publish(xcTask, xcTask.getMqExchange(), xcTask.getMqRoutingkey());

                LOGGER.info("send choose task id: {}", xcTask.getId());
            }

        }
    }

    /**
     * 接收选课响应结果
     * @param task
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE})
    public void receiveFinishChoosecourseTask(XcTask task, Message message, Channel channel) throws IOException {
        LOGGER.info("receiveChoosecourseTask...{}", task.getId());

        // 收到的消息id
        String id = task.getId();

        // 删除任务，添加历史任务
        taskService.finishTask(id);
    }


    /**
    //@Scheduled(fixedRate = 5000) // 上次执行开始时间后5秒执行，如果上次执行没有结束，则等起结束后执行
    // @Scheduled(fixedDelay = 1000) // 上次执行完毕后5秒后执行
    public void task1 () throws InterruptedException {
        LOGGER.info("~~~~~~~~~~~~~~ 1: 开始 ~~~~~~~~~~~~~~~~~~~");

        Thread.sleep(2000);

        LOGGER.info("~~~~~~~~~~~~~~ 1: 结束 ~~~~~~~~~~~~~~~~~~~");

    }

    // @Scheduled(fixedDelay = 1000) // 上次执行完毕后5秒后执行
    public void task2 () throws InterruptedException {
        LOGGER.info("~~~~~~~~~~~~~~ 2: 开始 ~~~~~~~~~~~~~~~~~~~");

        Thread.sleep(2000);

        LOGGER.info("~~~~~~~~~~~~~~ 2: 结束 ~~~~~~~~~~~~~~~~~~~");

    }
    */

}
