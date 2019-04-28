package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.CourseView;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Liucheng
 * @date 2019/4/17 14:09
 */
@Service
public class CourseService {

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;


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

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Autowired
    private CoursePubRepository coursePubRepository;

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
            courseBase.setStatus("202001");
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
        courseBaseById.setStatus(courseBase.getStatus());
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

    /**
     * 添加课程图片：每个课程只能有一张课程图片
     * 主键id就是课程id
     * @param courseId
     * @param pic
     * @return
     */
    @Transactional
    public ResponseResult saveCoursePic(String courseId, String pic) {
        // 查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);

        CoursePic coursePic = null;

        if (picOptional.isPresent()) {
            coursePic = picOptional.get();
        }

        // 如果没有课程图片则新建，如果有则更新
        if (coursePic == null) {
            coursePic = new CoursePic();
        }

        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);

        // 保存/更新课程图片
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据id查询课程图片
     * @param courseId
     * @return
     */
    public CoursePic findCoursepic(String courseId) {
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        return byId.orElse(null);
    }

    /**
     * 删除课程图片
     * @param courseId
     * @return
     */
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        // 执行删除，返回1表示删除成功，返回0表示删除失败
        long result = coursePicRepository.deleteByCourseid(courseId);

        if (result > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 课程视图查询
     * @param courseId
     * @return
     */
    public CourseView getCourseView(String courseId) {
        CourseView courseView = new CourseView();

        // 查询课程的基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        optional.ifPresent(courseView::setCourseBase);

        // 查询课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseId);
        courseMarketOptional.ifPresent(courseView::setCourseMarket);

        // 查询课程图片信息
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        picOptional.ifPresent(courseView::setCoursePic);

        // 查询课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    /**
     * 根据课程id查询课程基本信息
     * @param courseId
     * @return
     */
    public CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);

        if (baseOptional.isPresent()) {
            return baseOptional.get();
        }

        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);

        return null;
    }

    /**
     * 课程预览
     * @param courseId
     * @return
     */
    public CoursePublishResult preview(String courseId) {

        // 封装页面信息
        CmsPage cmsPage = setCmsPageMessage(courseId);

        // 远程请求cms保存/更新页面信息
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);

        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }

        // 页面Id
        String pageId = cmsPageResult.getCmsPage().getPageId();

        // 页面url
        String pageUrl = previewUrl + pageId;

        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    /**
     * 封装cms中的信息
     * @param courseId
     */
    private CmsPage setCmsPageMessage(String courseId) {
        CourseBase one = this.findCourseBaseById(courseId);

        // 发布课程预览/发布页面
        CmsPage cmsPage = new CmsPage();

        // 课程预览/发布站点
        cmsPage.setSiteId(publish_siteId);

        // 模板
        cmsPage.setTemplateId(publish_templateId);

        // 页面名称
        cmsPage.setPageName(courseId + ".html");

        // 页面别名
        cmsPage.setPageAliase(one.getName());

        // 页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);

        // 页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);

        // 数据获取url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);

        return cmsPage;
    }

    /**
     * 课程发布
     * @param courseId
     * @return
     */
    @Transactional
    public CoursePublishResult publish(String courseId) {

        // 课程信息
        CourseBase one = this.findCourseBaseById(courseId);

        // 发布课程详情页面
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(this.setCmsPageMessage(courseId));

        if (!cmsPostPageResult.isSuccess()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }

        // 更新课程状态
        CourseBase courseBase = saveCoursePubState(courseId, "202002");

        // 课程索引信息,保存在course_pub表中，备用
        CoursePub coursePub = createCoursePub(courseId);
        CoursePub newCoursePub = saveCoursePub(courseId, coursePub);
        if (newCoursePub == null) {
            // 创建课程索引信息失败
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CREATE_INDEX_ERROR);
        }

        // 课程缓存

        // 页面url
        return new CoursePublishResult(CommonCode.SUCCESS, cmsPostPageResult.getPageUrl());

    }

    /**
     * 跟新课程发布状态
     * @param courseId
     * @return
     */
    private CourseBase saveCoursePubState(String courseId, String statusCode) {
        CourseBase courseBase = this.findCourseBaseById(courseId);

        // 更新发布状态
        courseBase.setStatus(statusCode);

        CourseBase save = courseBaseRepository.save(courseBase);

        return save;
    }


    /**
     * 保存CoursePub 用于搜索索引库的数据创建
     * @param id
     * @param coursePub
     * @return
     */
    public CoursePub saveCoursePub(String courseId, CoursePub coursePub) {

        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }

        CoursePub coursePubNew = null;
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(courseId);

        if (coursePubOptional.isPresent()) {
            coursePubNew = coursePubOptional.get();
        }

        if (coursePubNew == null) {
            coursePubNew = new CoursePub();
        }

        BeanUtils.copyProperties(coursePub, coursePubNew);

        // 设置主键
        coursePubNew.setId(courseId);

        // 更新时间戳为最新时间
        coursePubNew.setTimestamp(new Date());

        // 发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);

        coursePubRepository.save(coursePubNew);

        return coursePubNew;
    }

    /**
     * 创建coursePub对象
     * @param courseId
     * @return
     */
    private CoursePub createCoursePub(String courseId) {
        CoursePub coursePub = new CoursePub();
        coursePub.setId(courseId);

        // 基础信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);

        if (courseBaseOptional.isPresent()) {
            CourseBase courseBase = courseBaseOptional.get();
            BeanUtils.copyProperties(courseBase, coursePub);
        }

        // 查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }

        // 查询课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(courseId);
        if (marketOptional.isPresent()) {
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }

        // 课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        // 将课程计划转换为json
        String teachplanJson = JSON.toJSONString(teachplanNode);

        coursePub.setTeachplan(teachplanJson);

        return coursePub;
    }

}
