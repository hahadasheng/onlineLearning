package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Liucheng
 * @date 2019/4/18 10:34
 */
@Service
public class SysDictionaryService {

    @Autowired
    private SysDictionaryRepository sysDictionaryRepository;

    /**
     * 根据类型查询数据字典
     * @param dType
     * @return
     */
    public SysDictionary findByDType(String dType) {
        return sysDictionaryRepository.findByDType(dType);
    }
}
