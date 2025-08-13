package com.example.transfer.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationFilter implements Filter {
    public static final String HEADER = "X-Request-Id";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse w = (HttpServletResponse) res;
        String id = Optional.ofNullable(r.getHeader(HEADER)).orElse(UUID.randomUUID().toString());
        MDC.put("rid", id);
        w.setHeader(HEADER, id);
        try {
            chain.doFilter(req, res);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            MDC.remove("rid");
        }
    }
}