package kr.hhplus.be.server.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class PathCheckInterceptor implements HandlerInterceptor {

    @Value("${spring.application.version}")
    private int applicationVersion;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        String prefix = "/api/v" + applicationVersion;

        if (!requestURI.contains(prefix)) {
            log.error("Invalid access path. Request URI : {}", requestURI);
            return false;
        }

        return true;
    }

}
