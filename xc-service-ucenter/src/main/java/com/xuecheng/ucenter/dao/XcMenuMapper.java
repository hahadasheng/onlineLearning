package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 根据用户id查询用户权限信息
 * @author Liucheng
 * @date 2019/4/26 14:57
 */
@Mapper
public interface XcMenuMapper {

    /**
     * 根据用户id查询用户的权限信息
     * @param userId
     * @return
     */
    public List<XcMenu> selectPermissionByUserId (String userId);
}
