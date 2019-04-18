package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void xxx() {
        System.out.println("??");
    }

    /**
     * 分页查询测试
     */
    @Test
    public void testFindPage() {
        // 从0开始
        int page = 0;

        // 每页记录数
        int size = 20;

        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(all);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }

    /**
     * 添加
     */
    @Test
    public void testInsert() {
        // 定义实体类
        CmsPage cmsPage = new CmsPage();

        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());

        List<CmsPageParam> cmsPageParams = new ArrayList<>();

        CmsPageParam cmsPageParam = new CmsPageParam();

        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);

        cmsPage.setPageParams(cmsPageParams);

        cmsPageRepository.save(cmsPage);

        System.out.println(cmsPage);
    }

    /**
     * 删除
     */
    @Test
    public void testDelete() {
        cmsPageRepository.deleteById("5cac877ff82e1837c44ea58e");
    }

    /**
     * 修改
     */
    @Test
    public void testUpdate() {
        // 1. 先查询
        Optional<CmsPage> optional = cmsPageRepository.findById("5cac8a52f82e182ab07ced0c");

        // Optional是jdk1.8引入的类型, 判断对象是否为null,将空指针处理标准化
        if(optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            cmsPage.setPageName("测试页面更新啦！");
            // 2. 再修改
            cmsPageRepository.save(cmsPage);
        }

    }

    /**
     * 方法名查询测试
     */
    @Test
    public void testFunName() {
        CmsPage byPageName = cmsPageRepository.findByPageName("测试页面更新啦！");
        System.out.println(byPageName);
    }

    /**
     * 自定义条件查询测试
     */
    @Test
    public void testFindAll() {

        // bean
        CmsPage cmsPage = new CmsPage();
        // 站点id
        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        // 模板id
        cmsPage.setTemplateId("5a962c16b00ffc514038fafd");
        // cmsPage.setPageAliase("分类导航");


        // 条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        /** 页面别名模糊查询，需要自定义字符串的匹配器实现模糊查询
         * ExampleMatcher.GenericPropertyMatchers.contains(); 包含
         * ExampleMatcher.GenericPropertyMatchers.startsWith(); 开头匹配
         */

        // 创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

        // 分页设置, 从0 开始
        Pageable pageable = new PageRequest(0, 10);

        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        System.out.println("~~~~~~~~~~~~~~~~");
        System.out.println(all);
        System.out.println("~~~~~~~~~~~~~~~~");

    }
}
