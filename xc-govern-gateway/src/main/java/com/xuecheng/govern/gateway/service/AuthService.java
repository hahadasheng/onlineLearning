package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Liucheng
 * @date 2019/4/25 18:13
 */
@Service
public class AuthService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询身份令牌
     * @param request
     * @return
     */
    public String getTokenFromCookie(HttpServletRequest request) {
        Map<String, String> cookieMap = CookieUtil.readCookie(request, "uid");

        String accessToken = cookieMap.get("uid");

        if (StringUtils.isEmpty(accessToken)) {
            return null;
        }

        return accessToken;
    }

    /**
     * 从header中查询jwt令牌
     * @param request
     * @return
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (StringUtils.isEmpty(authorization)) {
            // 拒绝访问
            return null;
        }

        if (!authorization.startsWith("Bearer ")) {
            // 拒绝访问
            return null;
        }

        return authorization;
    }

    /**
     * 查询令牌的有效期
     * 由于令牌存储时采用 String序列化策略，所以这里用 StringRedisTemplate来查询，使用RedisTemplate无
     * 法完成查询。
     * @param accessToken
     * @return
     */
    public long getExpire(String accessToken) {
        // token 在redis中的key
        String key = "user_token:" + accessToken;

        Long expire = stringRedisTemplate.getExpire(key);

        return expire;
    }
}
