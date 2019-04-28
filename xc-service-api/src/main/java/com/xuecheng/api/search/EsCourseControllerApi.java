package com.xuecheng.api.search;

import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;

/**
 * @author Liucheng
 * @date 2019/4/23 19:38
 */
@Api(value = "课程搜索", description = "课程搜索", tags = "课程搜索")
public interface EsCourseControllerApi {

    @ApiOperation("课程搜索")
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam) throws IOException;
}
