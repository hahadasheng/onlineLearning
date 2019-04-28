package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.CourseView;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Liucheng
 * @date 2019/4/17 14:15
 */
@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {

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
    @PreAuthorize("hasAuthority('course_find_list')")
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(
            @PathVariable("page") int page,
            @PathVariable("size") int size,
            CourseListRequest courseListRequest) {

        // 调用封装的工具类取出用户的信息
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);

        if (userJwt == null) {
            ExceptionCast.cast(CommonCode.UNAUTHENTICATED);
        }

        courseListRequest.setCompanyId(userJwt.getCompanyId());

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
    @PreAuthorize("hasAuthority('course_get_baseinfo')")
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

    /**
     * 保存课程图片
     * @param courseId
     * @param pic
     * @return
     */
    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(
            @RequestParam("courseId") String courseId,
            @RequestParam("pic") String pic) {
        return courseService.saveCoursePic(courseId, pic);
    }

    /**
     * 获取课程图片信息
     * @param courseId
     * @return
     */
    @PreAuthorize("hasAuthority('course_get_pic')")
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursepic(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    /**
     * 课程视图查询
     * @param courseId 课程id
     * @return
     */
    @Override
    @GetMapping("/courseview/{courseId}")
    public CourseView courseView(
            @PathVariable("courseId") String courseId) {
        return courseService.getCourseView(courseId);
    }

    /**
     * 课程预览
     * @param id
     * @return
     */
    @Override
    @PostMapping("/preview/{courseId}")
    public CoursePublishResult preview(@PathVariable("courseId") String courseId) {
        return courseService.preview(courseId);
    }

    /**
     * 发布课程
     * @param courseId
     * @return
     */
    @Override
    @PostMapping("/publish/{courseId}")
    public CoursePublishResult publish(@PathVariable("courseId") String courseId) {
        return courseService.publish(courseId);
    }


}
