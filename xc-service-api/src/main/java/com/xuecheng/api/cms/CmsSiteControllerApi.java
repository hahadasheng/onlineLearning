package com.xuecheng.api.cms;

import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="cms站点管理接口", description = "cms站点管理接口，提供站点的增、删、改、查")
public interface CmsSiteControllerApi {

    /**
     * 站点查询
     */
    @ApiOperation("查询所有的站点信息")
    public QueryResponseResult findAll();
}