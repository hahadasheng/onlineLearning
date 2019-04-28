package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * @author Liucheng
 * @date 2019/4/27 20:38
 */
public interface XcTaskRepository extends JpaRepository<XcTask, String> {

    /**
     * 取出指定时间之前的记录
     * @param pageable
     * @param updateTime
     * @return
     */
    Page<XcTask> findByUpdateTimeBefore(Pageable pageable, Date updateTime);

    /**
     * 更新任务处理时间
     * @param id
     * @param updateTime
     * @return
     */
    @Modifying
    @Query("update XcTask t set t.updateTime = :updateTime where t.id = :id")
    public int updateTaskTime(@Param(value = "id") String id, @Param(value = "updateTime") Date updateTime);


    /**
     * 使用乐观锁方式校验任务id和版本号是否匹配，匹配则版本号加1
     * @param id
     * @param version
     * @return
     */
    @Modifying
    @Query("update XcTask t set t.version = :version + 1 where t.id = :id and t.version = :version")
    public int updateTaskVersion(@Param(value = "id") String id, @Param(value = "version") int version);

}
