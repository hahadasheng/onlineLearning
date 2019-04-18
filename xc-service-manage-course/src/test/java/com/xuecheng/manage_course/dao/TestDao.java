package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.system.SysDictionary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sound.midi.Soundbank;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    SysDictionaryRepository sysDictionaryRepository;

    @Test
    public void testCourseBaseRepository(){
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }

    }

    @Test
    public void testCourseMapper(){
        CourseBase courseBase = courseMapper.findCourseBaseById("402885816240d276016240f7e5000002");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(courseBase);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~");

    }

    /**
     * 测试分页
     */
    @Test
    public void testPageHelper() {
        PageHelper.startPage(1, 10);
        CourseListRequest courseListRequest = new CourseListRequest();
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> result = courseListPage.getResult();
        System.out.println("~~~~~~~~~~");
        System.out.println(result.get(0).getId());
        System.out.println("~~~~~~~~~~");

    }

    /**
     * 测试课程分类查询
     */
    @Test
    public void testCourseCategory() {
        CategoryNode list = categoryMapper.findList();
        System.out.println(list);
    }

    /**
     * 测试课程级别数据字典
     */
    @Test
    public void testSysDic() {
        SysDictionary type = sysDictionaryRepository.findByDType("200");
        System.out.println(type);
    }
}
