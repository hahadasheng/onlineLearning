package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 数据字典管理
 * @author Liucheng
 * @date 2019/4/18 10:24
 */
public interface SysDictionaryRepository extends MongoRepository<SysDictionary, String> {

    /**
     * 根据类型名查询数据字典
     * @param dType
     * @return
     */
    public SysDictionary findByDType(String dType);

}
