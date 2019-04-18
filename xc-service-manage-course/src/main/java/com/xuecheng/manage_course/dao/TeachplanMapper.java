package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Liucheng
 * @date 2019/4/17 11:50
 */
@Mapper
public interface TeachplanMapper {

    /**
     * 根据课程id查询课程计划列表
     * @param courseId
     * @return
     */
    public TeachplanNode selectList(String courseId);
}
