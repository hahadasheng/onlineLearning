package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Liucheng
 * @date 2019/4/18 9:48
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询课程分类列表
     * @return
     */
    public CategoryNode findList() {
        return categoryMapper.findList();
    }
}
