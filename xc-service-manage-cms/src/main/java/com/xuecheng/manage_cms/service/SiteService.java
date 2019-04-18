package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService {

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    /**
     * 查询所有的站点信息
     * @return
     */
    public QueryResponseResult findAll() {

        // 分页查询
        List<CmsSite> all = cmsSiteRepository.findAll();

        // 封装返回对象
        QueryResult<CmsSite> cmsSiteQueryResult = new QueryResult<CmsSite>();
        cmsSiteQueryResult.setList(all);
        cmsSiteQueryResult.setTotal(all.size());

        return new QueryResponseResult(CommonCode.SUCCESS, cmsSiteQueryResult);
    }
}
