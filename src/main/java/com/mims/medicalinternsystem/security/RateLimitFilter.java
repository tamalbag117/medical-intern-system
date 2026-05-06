package com.mims.medicalinternsystem.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitFilter implements Filter {

    private static final Logger log =
            LoggerFactory.getLogger(RateLimitFilter.class);

    // ✅ Enterprise-safe values
    private static final int MAX_REQUESTS = 120;

    private static final long TIME_WINDOW = 60_000;

    private static final long CLEANUP_AGE =
            TIME_WINDOW * 5;

    private final ConcurrentMap<String, RequestInfo>
            requestMap = new ConcurrentHashMap<>();

    static class RequestInfo {

        volatile int count;

        volatile long timestamp;

        RequestInfo(int count, long timestamp) {
            this.count = count;
            this.timestamp = timestamp;
        }
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req =
                (HttpServletRequest) request;

        HttpServletResponse res =
                (HttpServletResponse) response;

        String path = req.getRequestURI();

        // ✅ Skip websocket + auth + health
        if (shouldSkip(path)) {

            chain.doFilter(request, response);

            return;
        }

        long now = Instant.now().toEpochMilli();

        // ✅ cleanup old entries
        cleanup(now);

        String ip = getClientIp(req);

        RequestInfo info =
                requestMap.compute(ip, (key, old) -> {

                    // new IP
                    if (old == null) {
                        return new RequestInfo(1, now);
                    }

                    // reset after window
                    if (now - old.timestamp > TIME_WINDOW) {

                        old.count = 1;

                        old.timestamp = now;

                        return old;
                    }

                    // increment
                    old.count++;

                    return old;
                });

        // ✅ rate limit exceeded
        if (info.count > MAX_REQUESTS) {

            log.warn(
                    "Rate limit exceeded for IP: {}",
                    ip
            );

            res.setStatus(429);

            res.setContentType(
                    "application/json"
            );

            res.getWriter().write(
                    """
                    {
                      "error":"Too many requests",
                      "message":"Please slow down"
                    }
                    """
            );

            return;
        }

        chain.doFilter(request, response);
    }

    // ✅ SKIP IMPORTANT ROUTES
    private boolean shouldSkip(String path) {

        return path.startsWith("/ws")
                || path.startsWith("/api/auth")
                || path.startsWith("/health")
                || path.startsWith("/actuator");
    }

    // ✅ CLEANUP OLD IPS
    private void cleanup(long now) {

        requestMap.entrySet()
                .removeIf(entry ->
                        now - entry.getValue().timestamp
                                > CLEANUP_AGE
                );
    }

    // ✅ REAL CLIENT IP SUPPORT
    private String getClientIp(
            HttpServletRequest request
    ) {

        String xfHeader =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (xfHeader == null
                || xfHeader.isBlank()) {

            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0];
    }
}