package com.xuecheng.order.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;

/**
 * 异步任务配置类，需要配置线程池实现多线程调度任务
 * @author Liucheng
 * @date 2019/4/27 17:58
 */
@Configuration
@EnableScheduling
public class AsyncTaskConfig implements SchedulingConfigurer, AsyncConfigurer {

    // 线程池线程数量
    private int corePoolSize = 5;

    /**
     * 任务调度线程池
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler () {

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // 初始化线程池
        scheduler.initialize();

        // 线程池容量
        scheduler.setPoolSize(corePoolSize);

        return scheduler;
    }

    @Override
    public Executor getAsyncExecutor() {
        Executor executor = taskScheduler();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler());
    }
}
