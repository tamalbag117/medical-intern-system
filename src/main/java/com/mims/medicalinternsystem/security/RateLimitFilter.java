package com.mims.medicalinternsystem.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS = 60; // 🔥 increase for frontend apps
    private static final long TIME_WINDOW = 60_000; // 1 minute

    private final ConcurrentMap<String, RequestInfo> requestMap = new ConcurrentHashMap<>();

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
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // 🔥 SKIP RATE LIMIT FOR IMPORTANT ROUTES
        if (path.startsWith("/ws") ||
                path.startsWith("/api/auth") ||
                path.startsWith("/health")) {

            chain.doFilter(request, response);
            return;
        }

        String ip = req.getRemoteAddr();
        long now = Instant.now().toEpochMilli();

        RequestInfo info = requestMap.getOrDefault(ip, new RequestInfo(0, now));

        if (now - info.timestamp > TIME_WINDOW) {
            info = new RequestInfo(1, now);
        } else {
            info.count++;
        }

        requestMap.put(ip, info);

        if (info.count > MAX_REQUESTS) {
            res.setStatus(429);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Too many requests\"}");
            return; // 🔥 CRITICAL FIX
        }

        chain.doFilter(request, response);
    }
}