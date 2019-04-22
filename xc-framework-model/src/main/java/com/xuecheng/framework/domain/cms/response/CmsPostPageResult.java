package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Liucheng
 * @date 2019/4/21 18:30
 */
@Data
@ToString
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {

    String pageUrl;

    public CmsPostPageResult(ResultCode resultCode, String pageUrl) {
        super(resultCode);

        this.pageUrl = pageUrl;
    }

}
