package top.ehre.mod.security.authentication;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public class UsernamePasswordFilter extends AbstractAuthenticationProcessingFilter {
    private boolean postOnly = true;

    public UsernamePasswordFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    public UsernamePasswordFilter(AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher("/login", "POST"), authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            // 使用fastjson解析JSON请求体
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
            } catch (IOException e) {
                throw new BadCredentialsException("Error reading JSON string", e);
            }
            JSONObject json = JSON.parseObject(sb.toString());
            if (json == null) throw new BadCredentialsException("Error reading JSON string");

            // 从JSON中获取用户id和密码
            String username = json.getString("username");
            String password = json.getString("password");
            username = username != null ? username : "";
            password = password != null ? password : "";
            UsernamePasswordToken authRequest = new UsernamePasswordToken(username, password);
            // 设置细节：sessionID和remoteURL
            this.setDetails(request, authRequest);
            // 传给manager让它找一个可以给这个token认证的provider， 如果认证成功了就返回成功认证的AuthenticationToken
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
