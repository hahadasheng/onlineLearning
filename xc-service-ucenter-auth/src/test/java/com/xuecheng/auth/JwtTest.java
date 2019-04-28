package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liucheng
 * @date 2019/4/24 15:54
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JwtTest {

    /**
     * 生成一个jwt令牌
     */
    @Test
    public void testCreateJwt() {
        // 证书文件
        String keyLocation = "xc.keystore";

        // 秘钥库密码
        String keystorePassword = "xuechengkeystore";

        // 秘钥的密码，此密码和别名要匹配
        String keypassword = "xuecheng";

        // 秘钥别名
        String alias = "xckey";

        // 访问证书路径
        ClassPathResource resource = new ClassPathResource(keyLocation);

        // 秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, keystorePassword.toCharArray());

        // 秘钥对(秘钥和公钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keypassword.toCharArray());

        // 私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey)keyPair.getPrivate();

        // 定义payload信息
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("name", "晓庆");

        // 生成jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));

        // 取出jwt令牌
        String token = jwt.getEncoded();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(token);
        /*
eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoi5pmT5bqGIn0.TsrV4LuiX3xO8vgo65wYe5riOJOeQh_ad81DuXGe5F8S0Uzv_6MxTpqIk0M1eSxus1-yMsBuV1Jnf4i9jpYzBEv-yZjk16-UA_kvuSmMzXW2bdf-so5Hi2mJs6jyYtKsh-i4GV3TqICIs4MYtTvHm7DkSpARepTCglkFzYy1ZT8FK1ySVJ-KfwZ74VsJz4i7XTcvgeIa6ncRj-nm16GLzQiFoJ-L5sd2LV_W4lSwAEPPixkd5U1MFUB5Cp7RXGyd5JMYi24I3DIM44Ca1pgJfWwSkQre7SQh-YVENv8r7oP3GCFDVdv6wYtmr_9Bvt0kEBdRKyFFT651lTA7logCqw

        */
    }

    /**
     * 资源服务使用公钥验证jwt的合法性，并对jwt解码
     */
    @Test
    public void testVerify() {
        // jwt 令牌内容
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiYWRtaW4iLCJzY29wZSI6WyJhcHAiXSwibmFtZSI6Iuezu-e7n-euoeeQhuWRmCIsInV0eXBlIjpudWxsLCJpZCI6IjQ4IiwiZXhwIjoxNTU2MzY5NDQwLCJhdXRob3JpdGllcyI6WyJ4Y19zeXNtYW5hZ2VyX2RvYyIsInhjX3N5c21hbmFnZXJfdXNlcl92aWV3IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZSIsImNvdXJzZV9maW5kX2xpc3QiLCJ4Y19zeXNtYW5hZ2VyX3VzZXJfYWRkIiwieGNfc3lzbWFuYWdlcl91c2VyX2RlbGV0ZSIsInhjX3N5c21hbmFnZXJfdXNlciIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYmFzZSIsImNvdXJzZV9nZXRfYmFzZWluZm8iLCJ4Y19zeXNtYW5hZ2VyIiwieGNfc3lzbWFuYWdlcl9sb2ciLCJ4Y19zeXNtYW5hZ2VyX3VzZXJfZWRpdCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYWRkIl0sImp0aSI6ImM5MjZlZmNlLWViODQtNDZmNS1hMWJkLWFiOWMzZGExMzFiMyIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.HMCBTd_MPs25Ak6toebXyK3i2840E7zxDzrv4yEDerY7bpqZDXghFhwHSxhnJGaKeZRYH48gfzm19PUzC77iWYJd5nHFCxd81n6pQJgyuv15E_JxTxGnY6JzufaSMyIAFh1-yGHV4sH-jd-8Vlp6MZ6Gp5xp7jt10hIKasfRquCQ6uKZmbTPM_ABIP0u4EvMdcxX8DHDbzsebc6W67vgYZkQ7U_JfuwL69GijZa0bwdH6U2z6mC4Tb5J8CVpI3Myc-gqX7aNHaq0VqllMH0sMxHCAUPxS0gtrz6B3OxN0mieBoLOkH7rn4gwIcFsF0CrxYDu2Ydp3sHl9yK4xz5Rvw";

        // 公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";

        // 校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        // 获取jwt原始内容
        String claims = jwt.getClaims();

        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(claims);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n");


    }


}
