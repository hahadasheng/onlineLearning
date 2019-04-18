package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 课程分类计划
 * @author Liucheng
 * @date 2019/4/18 9:17
 */
@Api(value = "课程分类管理", description = "课程分类管理", tags = {"课程分类管理"})
public interface CategoryControllerApi {

    @ApiOperation("查询分类")
    public CategoryNode findList();
}
