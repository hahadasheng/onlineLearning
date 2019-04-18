package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Category;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 课程分类信息
 * @author Liucheng
 * @date 2019/4/18 9:39
 */
@Mapper
public interface CategoryMapper {

    /**
     * 查询课程分类分级列表
     * @return
     */
    public CategoryNode findList();

}
