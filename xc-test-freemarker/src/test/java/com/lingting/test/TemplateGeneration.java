package com.lingting.test;

import com.xuecheng.test.freemarker.model.Student;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

@SpringBootConfiguration
@RunWith(SpringRunner.class)
public class TemplateGeneration {

    private Map getMap() {
        HashMap<String, Object> map = new HashMap<>();
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
        return map;
    }

    /**
     * 根据模板文件生成html文件
     * @throws Exception
     */
    @Test
    public void testGenerateHtml() throws Exception {

        // 创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());

        // 设置模板路径
        String classPath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classPath + "/templates"));

        // 设置字符集
        configuration.setDefaultEncoding("utf-8");

        // 加载模板
        Template template = configuration.getTemplate("test1.ftl");

        // 数据模型
        Map<String, Object> map = getMap();

        // 静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        // 输出到文件中
        InputStream inputStream = IOUtils.toInputStream(content);

        // 目标文件
        FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\ALearnCityProject\\ServiceWorkSpace\\xc-test-freemarker\\src\\test\\resources\\test1.html"));

        int copy = IOUtils.copy(inputStream, fileOutputStream);
    }

    /**
     * 根据字符串生成 html 文件
     */
    @Test
    public void generateHtmlByString() throws Exception {
        // 创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());

        // 使用简单的字符串作为模板
        String templateString = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Hello World!</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "Hello ${name}!\n" +
                "</body>\n" +
                "</html>";

        // 模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", templateString);

        // 设置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);

        // 得到模板
        Template template = configuration.getTemplate("template", "utf-8");

        // 数据模型
        Map<String, Object> map = getMap();

        // 静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);

        // 输出到文件
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\ALearnCityProject\\ServiceWorkSpace\\xc-test-freemarker\\src\\test\\resources\\test2.html"));
        IOUtils.copy(inputStream, fileOutputStream);
    }


}
