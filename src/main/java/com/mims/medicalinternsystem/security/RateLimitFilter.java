package com.mims.medicalinternsystem.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS = 20;
    private static final long TIME_WINDOW = 60_000; // 1 minute

    private final Map<String, RequestInfo> requestMap = new HashMap<>();

    static class RequestInfo {
        int count;
        long timestamp;

        RequestInfo(int count, long timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String ip = req.getRemoteAddr();

        long now = Instant.now().toEpochMilli();

        RequestInfo info = requestMap.getOrDefault(ip, new RequestInfo(0, now));

        if (now - info.timestamp > TIME_WINDOW) {
            // Reset after time window
            info = new RequestInfo(1, now);
        } else {
            info.count++;
        }

        requestMap.put(ip, info);

        if (info.count > MAX_REQUESTS) {
            HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(429);
            res.getWriter().write("Too many requests. Try again later.");
            return;
        }

        chain.doFilter(request, response);
    }
}