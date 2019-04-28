package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Liucheng
 * @date 2019/4/25 9:56
 */
@Service
public class UserService {

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    private XcMenuMapper xcMenuMapper;

    /**
     * 根据用户用户名查询用户信息
     * @param username
     * @return
     */
    public XcUser findXcUserByUserName (String username) {
        return xcUserRepository.findXcUserByUsername(username);
    }

    /**
     * 根据账号查询用户的信息，返回用户扩展信息
     * @param username
     * @return
     */
    public XcUserExt getUserExt (String username) {
        XcUserExt xcUserExt = new XcUserExt();

        XcUser xcUser = this.findXcUserByUserName(username);

        if (xcUser == null) {
            return null;
        }

        BeanUtils.copyProperties(xcUser, xcUserExt);

        // 用户id
        String userId = xcUserExt.getId();

        // 查询该用户拥有的权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        xcUserExt.setPermissions(xcMenus);

        // 查询所属的公司
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);

        if (xcCompanyUser != null) {
            String companyId = xcCompanyUser.getCompanyId();
            xcUserExt.setCompanyId(companyId);
        }

        return xcUserExt;
    }

}
