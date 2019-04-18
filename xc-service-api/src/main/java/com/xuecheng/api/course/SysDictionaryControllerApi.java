package com.xuecheng.api.course;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Liucheng
 * @date 2019/4/18 10:18
 */
@Api(value = "数据字典接口", description = "提供数据字典接口的管理,查询功能")
public interface SysDictionaryControllerApi {

    /**
     * 数据字典
     * @param type
     * @return
     */
    @ApiOperation("数据字典查询接口")
    public SysDictionary getByType(String type);
}
