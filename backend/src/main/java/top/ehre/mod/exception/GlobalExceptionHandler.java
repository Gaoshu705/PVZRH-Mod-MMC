package top.ehre.mod.exception;

import top.ehre.mod.util.IPUtil;
import top.ehre.mod.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public Result businessExceptionHandler(BusinessException e) {
        HttpServletRequest request = getHttpServletRequest();
        String ip = request == null ? "" : IPUtil.getIP(request);
        String requestURL = request == null ? "" : request.getRequestURL().toString();
        log.error("[{}] [{}] business error: {}", ip, requestURL, e.getMessage());
        return Result.fail(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(AuthorizationDeniedException.class)
    public Result authorizationDeniedExceptionHandler(Throwable e) {
        HttpServletRequest request = getHttpServletRequest();
        String ip = request == null ? "" : IPUtil.getIP(request);
        String requestURL = request == null ? "" : request.getRequestURL().toString();
        log.error("[{}] [{}] error: {}", ip, requestURL, e.getMessage());
        return Result.fail(104, "权限不足");
    }

    /**
     * 其他全部异常
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public Result errorHandler(Throwable e) {
        HttpServletRequest request = getHttpServletRequest();
        String ip = request == null ? "" : IPUtil.getIP(request);
        String requestURL = request == null ? "" : request.getRequestURL().toString();
        log.error("[{}] [{}] error: ", ip, requestURL, e);
        return Result.fail(e.toString());
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

}