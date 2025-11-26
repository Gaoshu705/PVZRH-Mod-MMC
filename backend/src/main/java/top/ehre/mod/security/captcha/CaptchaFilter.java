package top.ehre.mod.security.captcha;

import top.ehre.mod.security.authentication.handler.AuthFailureHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
public class CaptchaFilter extends OncePerRequestFilter {
    private RedisTemplate redisTemplate;
    private AuthFailureHandler authFailureHandler;

    public CaptchaFilter(RedisTemplate redisTemplate, AuthFailureHandler authFailureHandler) {
        this.redisTemplate = redisTemplate;
        this.authFailureHandler = authFailureHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ServletRequest requestWrapper = new BodyReaderCachingRequestWrapper(request);
        try {
            validate(requestWrapper);
        } catch (BadCredentialsException e) {
            authFailureHandler.onAuthenticationFailure(request, response, e);
            return;
        }
        filterChain.doFilter(requestWrapper, response);
    }

    // 校验验证码逻辑
    private void validate(ServletRequest request) throws BadCredentialsException {
        String code = null;
        String captchaOwner = null;
        Map<String, String> loginData;
        try {
            loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
        } catch (IOException e) {
            throw new BadCredentialsException("验证码读取失败!");
        }
        code = loginData.get("captchaCode");
        captchaOwner = loginData.get("captchaOwner");
        if (StringUtils.isEmpty(code)) {
            throw new BadCredentialsException("验证码不能为空!");
        }
        if (captchaOwner == null) {
            throw new BadCredentialsException("验证码已过期!");
        } else {
            String redisKey = "captcha:" + captchaOwner;
            String answer_code = (String) redisTemplate.opsForValue().get(redisKey);
            if (answer_code == null) {
                throw new BadCredentialsException("验证码已过期!");
            } else if (!code.equalsIgnoreCase(answer_code)) {
                throw new BadCredentialsException("验证码错误!");
            } else {
            }
        }

    }
}

class BodyReaderCachingRequestWrapper extends HttpServletRequestWrapper {
    private byte[] requestBody = null;//用于将流保存下来

    public BodyReaderCachingRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        requestBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);

        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}
