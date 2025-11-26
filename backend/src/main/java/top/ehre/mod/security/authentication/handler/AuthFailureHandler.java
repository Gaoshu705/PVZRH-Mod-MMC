package top.ehre.mod.security.authentication.handler;

import top.ehre.mod.util.Result;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public class AuthFailureHandler  implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        // 捕获了认证失败的异常
        // 返回失败信息
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/json");
        String jsonString = JSON.toJSONString(Result.fail(100, e.getMessage()));
        httpServletResponse.getWriter().println(jsonString);
        httpServletResponse.getWriter().flush();
    }
}
