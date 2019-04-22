package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 课程数据模型查询相应结果
 * @author Liucheng
 * @date 2019/4/21 9:28
 */
@Data
@ToString
@NoArgsConstructor
public class CourseView implements Serializable {

    // 基础信息
    CourseBase courseBase;

    // 课程营销
    CourseMarket courseMarket;

    // 课程图片
    CoursePic coursePic;

    // 教学计划
    TeachplanNode teachplanNode;



}
