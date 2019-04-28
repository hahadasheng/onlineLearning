package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Liucheng
 * @date 2019/4/24 21:52
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser, String> {

    /**
     * 根据用户id查询 用户和企业的关联信息
     * @param userId
     * @return
     */
    XcCompanyUser findByUserId(String userId);
}
