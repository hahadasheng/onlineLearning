package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Liucheng
 * @date 2019/4/17 14:52
 */
public interface TeachplanRepository extends JpaRepository<Teachplan, String> {

    /**
     * 根据课程id和父节点id查询出节点列表，使用此方法查询根节点
     * @param courseId
     * @param parentId
     * @return
     */
    public List<Teachplan> findByCourseidAndParentid(String courseId, String parentId);
}
