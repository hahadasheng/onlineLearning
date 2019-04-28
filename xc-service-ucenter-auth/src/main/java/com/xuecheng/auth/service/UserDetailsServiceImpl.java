package com.xuecheng.auth.service;

import com.xuecheng.auth.client.UserClient;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if(authentication==null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //密码
                String clientSecret = clientDetails.getClientSecret();
                return new User(username,clientSecret,AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        if (StringUtils.isEmpty(username)) {
            return null;
        }

        // 请求ucenter查询用户
        XcUserExt userext = userClient.getUserext(username);
        if (userext == null) {
            // 返回Null 表示用户不存在，Spring Security会抛出异常
            return null;
        }

        // 从数据库中查询用户的正确密码，Spring Security回去对比输入密码的正确性
        String password = userext.getPassword();

        // 指定用户的权限，
        List<String> permissionList = new ArrayList<>();
        List<XcMenu> permissions = userext.getPermissions();
        for (XcMenu xcMenu : permissions) {
            permissionList.add(xcMenu.getCode());
        }

        // permissionList.add("course_find_list");
        // permissionList.add("course_get_baseinfo");

        // 将权限串中间一逗号分隔
        String permissionString = StringUtils.join(permissionList.toArray(), ",");

        // UserJwt继承了Spring Security 提供 的User类，支持封装Jwt令牌
        UserJwt userDetails = new UserJwt(
                username,
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(permissionString));

        // 用户id
        userDetails.setId(userext.getId());
        // 用户名称
        userDetails.setName(userext.getName());
        // 用户头像
        userDetails.setUserpic(userext.getUserpic());
        // 用户所属企业id
        userDetails.setCompanyId(userext.getCompanyId());

        return userDetails;


        /**

        XcUserExt userext = new XcUserExt();
        userext.setUsername("lingting");
        userext.setPassword(new BCryptPasswordEncoder().encode("abc123"));


        userext.setPermissions(new ArrayList<XcMenu>());
        if(userext == null){
            return null;
        }
        //取出正确密码（hash值）
        String password = userext.getPassword();
        //这里暂时使用静态密码
//       String password ="123";
        //用户权限，这里暂时使用静态数据，最终会从数据库读取
        //从数据库获取权限
        List<XcMenu> permissions = userext.getPermissions();
        List<String> user_permission = new ArrayList<>();
        permissions.forEach(item-> user_permission.add(item.getCode()));
//        user_permission.add("course_get_baseinfo");
//        user_permission.add("course_find_pic");
        String user_permission_string  = StringUtils.join(user_permission.toArray(), ",");
        UserJwt userDetails = new UserJwt(username,
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(user_permission_string));

        userDetails.setId(userext.getId());
        userDetails.setUtype(userext.getUtype());//用户类型
        userDetails.setCompanyId(userext.getCompanyId());//所属企业
        userDetails.setName(userext.getName());//用户名称
        userDetails.setUserpic(userext.getUserpic());//用户头像

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username,
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(""));
                AuthorityUtils.createAuthorityList("course_get_baseinfo","course_get_list"));
        return userDetails;
        */
    }
}
