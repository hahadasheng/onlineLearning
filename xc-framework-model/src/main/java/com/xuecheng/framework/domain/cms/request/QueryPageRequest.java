package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;

@Data
public class QueryPageRequest extends RequestData {

    // 站点id
    private String siteId;

    // 页面id
    private String pageId;

    // 页面名称
    private String pageName;

    // 别名
    private String pageAliase;

    // 模板id
    private String templateId;
}
