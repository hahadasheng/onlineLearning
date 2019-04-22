package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Liucheng
 * @date 2019/4/20 9:21
 */
public interface CoursePicRepository extends JpaRepository<CoursePic, String> {

    /**
     * 根据课程id删除图片信息
     * @param courseId
     * @return
     */
    long deleteByCourseid(String courseId);
}
