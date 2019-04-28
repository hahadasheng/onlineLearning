package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Liucheng
 * @date 2019/4/24 21:48
 */
@Api(value = "用户中心", description = "用户管理中心")
public interface UcenterControllerApi {

    @ApiOperation("获取用户的一些拓展信息")
    public XcUserExt getUserext(String username);

}
