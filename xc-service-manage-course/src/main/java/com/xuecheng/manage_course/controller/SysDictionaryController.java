package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.SysDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Liucheng
 * @date 2019/4/18 10:20
 */
@RestController
@RequestMapping("/dictionary")
public class SysDictionaryController implements SysDictionaryControllerApi {

    @Autowired
    private SysDictionaryService sysDictionaryService;
    /**
     * 根据类型名称查询数据字典信息
     * @param type
     * @return
     */
    @Override
    @GetMapping("/get/{type}")
    public SysDictionary getByType(@PathVariable("type") String type) {
        return sysDictionaryService.findByDType(type);
    }
}
