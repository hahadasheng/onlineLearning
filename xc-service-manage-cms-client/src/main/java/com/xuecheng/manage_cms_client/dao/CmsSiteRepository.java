package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Liucheng
 * @date 2019/4/16 11:44
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite, String> {
}
