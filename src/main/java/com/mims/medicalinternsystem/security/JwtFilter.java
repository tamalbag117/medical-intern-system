package com.mims.medicalinternsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ PUBLIC ROUTES
        if (
                path.startsWith("/v3/api-docs") ||
                        path.startsWith("/swagger-ui") ||
                        path.startsWith("/swagger-ui.html") ||
                        path.startsWith("/health") ||
                        path.startsWith("/api/auth") ||
                        path.startsWith("/error") ||
                        path.startsWith("/ws")
        ) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String header =
                    request.getHeader("Authorization");

            // ✅ NO TOKEN
            if (
                    header == null ||
                            !header.startsWith("Bearer ")
            ) {

                filterChain.doFilter(request, response);
                return;
            }

            String token =
                    header.substring(7);

            String email =
                    jwtUtil.extractEmail(token);

            // ✅ AUTHENTICATE
            if (
                    email != null &&
                            SecurityContextHolder
                                    .getContext()
                                    .getAuthentication() == null
            ) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(email);

                // ✅ VALID TOKEN
                if (
                        jwtUtil.validateToken(
                                token,
                                userDetails
                        )
                ) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    auth.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                }
            }

            filterChain.doFilter(
                    request,
                    response
            );

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write("""
                {
                  "error": "Invalid or expired token"
                }
            """);
        }
    }
}