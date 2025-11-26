package top.ehre.mod.security.authentication.handler;

import top.ehre.mod.util.Result;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {
    RedisTemplate redisTemplate;

    public LogoutHandler(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        String token = httpServletRequest.getHeader("token");
        if (token == null) token = "";
        String[] split = token.split(":");
        Long delete = 0L;
        if (split.length == 2) {
            String username = split[0];
            delete = redisTemplate.opsForHash().delete("login:token:" + username, token);
        }
        String jsonString = JSON.toJSONString(Result.success("退出登录成功"));
        if (delete == null || delete == 0) {
            jsonString = JSON.toJSONString(Result.fail("退出登录失败"));
        }
        SecurityContextHolder.clearContext();
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/json");
        try {
            httpServletResponse.getWriter().println(jsonString);
            httpServletResponse.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
