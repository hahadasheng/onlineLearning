package com.xuecheng.api.cms;

import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="cms模板管理接口", description = "cms模板管理接口，提供模板的增、删、改、查")
public interface CmsTemplateControllerApi {

    /**
     * 模板查询
     */
    @ApiOperation("查询所有的模板信息")
    public QueryResponseResult findAll();
}
