package kr.hhplus.be.server.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        log.info("HTTP Method : {}, Request URI : {}, ContentType : {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), httpServletRequest.getContentType());

        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();

        if (!parameterMap.isEmpty()) {
            parameterMap.keySet().forEach(key -> {
                String[] value = parameterMap.get(key);
                log.info("Request Parameters >>> key: {} , value: {}", key, value);
            });
        }

        chain.doFilter(request, response);
    }

}
