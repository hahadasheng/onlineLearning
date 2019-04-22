package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Liucheng
 * @date 2019/4/20 16:47
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCloud {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testRibbon() {

        // 服务 id
        String serviceId = "XC-SERVICE-MANAGE-CMS";

        for (int i = 0; i < 10; i++) {
            // 通过服务id调用
            ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://" + serviceId +
                    "/cms/page/get/5a754adf6abb500ad05688d9", Map.class);

            Map body = forEntity.getBody();
            System.out.println(body);
        }
    }


    @Autowired
    private CmsPageClient cmsPageClient;

    @Test
    public void testFeign() {
        // 通过服务id调用cms的查询页面接口

        CmsPageResult byId = cmsPageClient.findById("5a754adf6abb500ad05688d9");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(byId);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");

    }








}
