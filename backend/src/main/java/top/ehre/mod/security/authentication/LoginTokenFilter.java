package top.ehre.mod.security.authentication;

import top.ehre.mod.system.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Component
public class LoginTokenFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;

    @Autowired
    @Lazy
    UserService userService;

    public LoginTokenFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String reqToken = httpServletRequest.getHeader("token");
        reqToken = reqToken == null ? "" : reqToken;
        String[] split = reqToken.split(":");
        if (split.length == 2) {
            String username = split[0];
            Long expirationTimestamp = (Long) redisTemplate.opsForHash().get("login:token:" + username, reqToken);
            long epochMilli = Instant.now().toEpochMilli();
            if (expirationTimestamp != null && epochMilli < expirationTimestamp) {
                UserInfo userInfo = userService.getUserInfo(username);
                Collection<? extends GrantedAuthority> authorities = userInfo.generateAuthorities();
                UsernamePasswordToken authenticationToken = new UsernamePasswordToken(username, reqToken, authorities);
                authenticationToken.setDetails(userInfo);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
