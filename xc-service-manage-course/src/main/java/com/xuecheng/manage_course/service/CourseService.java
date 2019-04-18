package com.xuecheng.manage_course.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

/**
 * @author Liucheng
 * @date 2019/4/17 14:09
 */
@Service
public class CourseService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private TeachplanRepository teachplanRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    /**
     * 查询课程计划
     * @param courseId
     * @return
     */
    public TeachplanNode findTeachplanList(String courseId) {
        return teachplanMapper.selectList(courseId);
    }

    /**
     * 获取课程根节点，如果没有则添加根节点
     * @param courseId
     * @return
     */
    public String getTeachplanRoot(String courseId) {

        // 校验课程id
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);

        if (!optional.isPresent()) {
            return null;
        }

        // 课程id的基本信息
        CourseBase courseBase = optional.get();

        // 取出课程计划根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");

        if (teachplanList == null || teachplanList.size() == 0) {
            // 如果该课程没有没有根节点，把该课程计划携带过来的课程id获取基本课程信息，然后写入到计划中，并作为根计划
            Teachplan teachplanRoot = new Teachplan();

            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setPname(courseBase.getName());
            teachplanRoot.setParentid("0");
            // 1级别
            teachplanRoot.setGrade("1");
            // 未发布
            teachplanRoot.setStatus("0");
            teachplanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }

        return teachplanList.get(0).getId();
    }

    /**
     * 添加课程计划，需要声明式事务控制
     * @param teachplan
     * @return
     */
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {

        // 校验课程id和课程计划名称
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
        StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        // 取出课程id
        String courseid = teachplan.getCourseid();

        // 取出父节点id
        String parentid = teachplan.getParentid();

        // 如果父节点为空，则获取根节点
        if (StringUtils.isEmpty(parentid)) {
            parentid = getTeachplanRoot(courseid);
        }

        if (parentid == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        // 取出父节点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentid);

        if (!teachplanOptional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        // 父节点
        Teachplan teachplanParent = teachplanOptional.get();

        // 父节点级别
        String parentGrade = teachplanParent.getGrade();

        // 设置父节点
        teachplan.setParentid(parentid);

        // 未发布
        teachplan.setStatus("0");

        // 子节点级别，根据父节点来判断
        if (parentGrade.equals("1")) {
            teachplan.setGrade("2");
        } else if (parentGrade.equals("2")) {
            teachplan.setGrade("3");
        }

        // 设置课程id
        teachplan.setCourseid(teachplanParent.getCourseid());
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    public QueryResult findCourseList(int page, int size, CourseListRequest courseListRequest) {
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);

        if (courseListPage == null) {
            return null;
        }

        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(courseListPage.getResult());
        queryResult.setTotal(courseListPage.getTotal());
        queryResult.setPage(courseListPage.getPageNum());

        return queryResult;
    }

    /**
     * 添加课程基本信息
     * @param courseBase
     * @return
     */
    public ResponseResult addCourseBase(CourseBase courseBase) {
        try {
            CourseBase save = courseBaseRepository.save(courseBase);
        } catch (Exception e) {
            ExceptionCast.cast(CourseCode.COURSE_ADDBASEE_RROR);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询课程基本信息
     * @param courseId
     * @return
     */
    public CourseBase getCourseBaseById(String courseId){
        Optional<CourseBase> byId = courseBaseRepository.findById(courseId);
        if (!byId.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_FIND_NOTEXIST);
        }

        return byId.get();
    }

    /**
     * 跟新课程基本信息
     * @param courseId
     * @param courseBase
     * @return
     */
    public ResponseResult updateCourseBase(String courseId, CourseBase courseBase) {
        // 首先进行查询，判断
        CourseBase courseBaseById = this.getCourseBaseById(courseId);

        if (courseBaseById == null) {
            ExceptionCast.cast(CourseCode.COURSE_FIND_NOTEXIST);
        }

        // 然后选择性更新
        courseBaseById.setName(courseBase.getName());
        courseBaseById.setUsers(courseBase.getUsers());
        courseBaseById.setGrade(courseBase.getGrade());
        courseBaseById.setStudymodel(courseBase.getStudymodel());
        courseBaseById.setMt(courseBase.getMt());
        courseBaseById.setSt(courseBase.getSt());
        courseBaseById.setDescription(courseBase.getDescription());

        try {
            courseBaseRepository.save(courseBaseById);
        } catch (Exception e) {
            ExceptionCast.cast(CourseCode.COURSE_ADDBASEE_RROR);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询课程营销信息
     * @param cousrseId
     * @return
     */
    public CourseMarket getCourseMarketById(String cousrseId) {
        Optional<CourseMarket> byId = courseMarketRepository.findById(cousrseId);
        if (!byId.isPresent()) {
            // 不能够抛出异常
            return null;
        }
        return byId.get();
    }

    /**
     * 修改课程营销信息
     * 课程营销的主键id与课程基本的营销信息id相同
     * @param courseId
     * @param courseMarket
     * @return
     */
    public ResponseResult updateCourseMarket(String courseId, CourseMarket courseMarket) {
        CourseBase courseBaseById = this.getCourseBaseById(courseId);

        // 验证基本信息表是否存在
        if (courseBaseById == null) {
            ExceptionCast.cast(CourseCode.COURSE_FIND_NOTEXIST);
        }

        CourseMarket courseMarketById = this.getCourseMarketById(courseId);

        // 如果课程信息不存在，
        if (courseMarketById == null) {
            /**
             添加课程营销信息 为什么要copy呢？直接保存不行吗？游离态？
             one = new CourseMarket();
             BeanUtils.copyProperties(courseMarket, one);
             */
            courseMarket.setId(courseId);
            try {
                courseMarketRepository.save(courseMarket);
                return new ResponseResult(CommonCode.SUCCESS);

            } catch (Exception e) {
                ExceptionCast.cast(CourseCode.COURSE_ADDBASEE_RROR);
            }
        }

        courseMarketById.setCharge(courseMarket.getCharge());
        courseMarketById.setValid(courseMarket.getValid());
        courseMarketById.setPrice(courseMarket.getPrice());
        courseMarketById.setExpires(courseMarket.getExpires());
        courseMarketById.setStartTime(courseMarket.getStartTime());
        courseMarketById.setEndTime(courseMarket.getEndTime());
        courseMarketById.setQq(courseMarket.getQq());
        courseMarketRepository.save(courseMarketById);

        return new ResponseResult(CommonCode.SUCCESS);
    }

}
