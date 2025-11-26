package top.ehre.mod.security.authentication.handler;

import top.ehre.mod.security.authentication.UserInfo;
import top.ehre.mod.util.Result;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public class AuthSuccessHandler  implements AuthenticationSuccessHandler {

    RedisTemplate redisTemplate;

    public AuthSuccessHandler(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        String username = (String) authentication.getPrincipal();
        UserInfo userInfo = (UserInfo) authentication.getDetails();
        String token = username + ":" + UUID.randomUUID().toString().replace("-", "");

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("userInfo", userInfo);
        responseMap.put("token", token);

        long expirationTimestamp = Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli();
        redisTemplate.opsForHash().put("login:token:" + username, token, expirationTimestamp);

        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/json");
        String jsonString = JSON.toJSONString(Result.success(responseMap));
        httpServletResponse.getWriter().println(jsonString);
        httpServletResponse.getWriter().flush();
    }
}
