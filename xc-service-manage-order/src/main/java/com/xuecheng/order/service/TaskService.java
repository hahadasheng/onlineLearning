package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Date;
import java.util.Optional;

/**
 * @author Liucheng
 * @date 2019/4/27 20:41
 */
@Service
public class TaskService {

    @Autowired
    private XcTaskRepository xcTaskRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    /**
     * 取出前n条任务，取出指定时间之前处理的任务
     * @param updateTime
     * @param n
     * @return
     */
    public List<XcTask> findTaskList (Date updateTime, int n) {
        // 设置分页参数，取出前n条记录
        Pageable pageable = new PageRequest(0, n);

        Page<XcTask> xcTasks = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);

        return xcTasks.getContent();
    }

    /**
     * 发送消息
     * @param xcTask
     * @param ex
     * @param routingKey
     */
    @Transactional
    public void publish (XcTask xcTask, String ex, String routingKey) {

        // 查询任务
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());

        if (taskOptional.isPresent()) {

            xcTask = taskOptional.get();

            // String exchange, String routingKey, Object object

            rabbitTemplate.convertAndSend(ex, routingKey, xcTask);

            // 更新任务时间为当前时间
            xcTask.setUpdateTime(new Date());
            xcTaskRepository.save(xcTask);
        }
    }

    /**
     * 使用乐观锁方法校验任务
     * @param taskId
     * @param version
     * @return
     */
    @Transactional
    public int getTask(String taskId, int version) {
        return xcTaskRepository.updateTaskVersion(taskId, version);
    }

    /**
     * 删除任务
     * @param taskId
     */
    @Transactional
    public void finishTask(String taskId) {
        Optional<XcTask> taskOptional = xcTaskRepository.findById(taskId);

        if (taskOptional.isPresent()) {
            XcTask xcTask = taskOptional.get();
            xcTask.setDeleteTime(new Date());
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }

}
