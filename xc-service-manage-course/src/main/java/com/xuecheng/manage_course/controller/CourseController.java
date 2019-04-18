package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Liucheng
 * @date 2019/4/17 14:15
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    private CourseService courseService;

    /**
     * 查询课程计划
     * @param courseId
     * @return
     */
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }

    /**
     * 添加课程计划
     * @param teachplan
     * @return
     */
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }

    /**
     * 分页查询课程信息
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(
            @PathVariable("page") int page,
            @PathVariable("size") int size,
            CourseListRequest courseListRequest) {

        return new QueryResponseResult(
                CommonCode.SUCCESS,
                courseService.findCourseList(page, size, courseListRequest));
    }

    /**
     * 添加课程
     * @param courseBase
     * @return
     */
    @Override
    @PostMapping("/coursebase/add")
    public ResponseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }


    /**
     * 获取课程基本信息
     * @param courseId
     * @return
     * @throws RuntimeException
     */
    @Override
    @GetMapping("/baseinfo/get/{courseid}")
    public CourseBase getCourseBaseById(@PathVariable("courseid") String courseId) throws RuntimeException {
        return courseService.getCourseBaseById(courseId);
    }


    /**
     * 跟新课程基础信息
     * @param courseId
     * @param courseBase
     * @return
     */
    @Override
    @PostMapping("/baseinfo/post/{courseId}")
    public ResponseResult updateCourseBase(
            @PathVariable("courseId") String courseId,
            @RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(courseId, courseBase);
    }

    /**
     * 查询课程营销信息
     * @param cousrseId
     * @return
     */
    @Override
    @GetMapping("/market/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    /**
     * 修改课程营销信息
     * @param id
     * @param courseMarket
     * @return
     */
    @Override
    @PostMapping("/market/post/{courseId}")
    public ResponseResult updateCourseMarket(
            @PathVariable("courseId") String courseId,
            @RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarket(courseId, courseMarket);
    }

}
