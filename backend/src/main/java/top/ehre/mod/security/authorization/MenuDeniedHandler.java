package top.ehre.mod.security.authorization;

import top.ehre.mod.util.Result;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public class MenuDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        String jsonString = JSON.toJSONString(Result.fail(104, accessDeniedException.getMessage()));
        response.getWriter().println(jsonString);
        response.getWriter().flush();
    }
}
