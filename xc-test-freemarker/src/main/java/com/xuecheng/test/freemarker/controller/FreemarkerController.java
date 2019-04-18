package com.xuecheng.test.freemarker.controller;

import com.xuecheng.test.freemarker.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 这里使用Freemarker 作为 ViewResolver，进行视图处理
 * 原理应该和SpringMVC框架类似
 * 模板文件必须放在resources/templates下面，规定的！
 */
@Controller
@RequestMapping("/freemarker")
public class FreemarkerController {

    @Autowired
    RestTemplate restTemplate;

    /**
     * Freemarker
     * @param map
     * @return
     */
    @RequestMapping("/test1")
    public String freemarker(Map<String, Object> map) {
        // 向数据模型中放数据
        map.put("name", "晓庆");
        map.put("today", new Date());
        map.put("point", 9999998);

        Student stu1 = new Student();
        stu1.setName("小明");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());

        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);
        stu2.setBirthday(new Date());

        List<Student> friends = new ArrayList<>();
        friends.add(stu1);

        stu2.setFriends(friends);
        stu2.setBestFriend(stu1);

        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);

        // 向数据模型中放数据
        map.put("stus", stus);

        // 准备map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);

        // 向数据模型中放数据
        map.put("stu1", stu1);
        map.put("stuMap", stuMap);

        // 返回模板文件名称
        return "test1";
    }

    /**
     * 测试轮播图模板
     * @param map
     * @return
     */
    @RequestMapping("/banner")
    public String generateBanner(Map<String, Object> map) {
        // 向数据模型中放数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);

        // 返回模板文件名称
        return "index_banner";
    }
}
