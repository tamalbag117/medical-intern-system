package com.mims.medicalinternsystem.config;

import com.mims.medicalinternsystem.security.JwtFilter;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ✅ CORS
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // ✅ DISABLE CSRF
                .csrf(csrf -> csrf.disable())

                // ✅ STATELESS SESSION
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ✅ AUTHORIZATION RULES
                .authorizeHttpRequests(auth -> auth

                        // ✅ PREFLIGHT REQUESTS
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // ✅ PUBLIC ROUTES
                        .requestMatchers(
                                "/",
                                "/health",
                                "/error",

                                "/api/auth/**",

                                "/ws/**",
                                "/ws/info/**",

                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // ✅ AI MODULE
                        .requestMatchers("/api/ai/**")
                        .hasAnyRole(
                                "ADMIN",
                                "DOCTOR",
                                "INTERN"
                        )

                        // ✅ LEAVE MODULE
                        .requestMatchers("/api/leave/**")
                        .authenticated()

                        // ✅ ATTENDANCE
                        .requestMatchers("/api/attendance/**")
                        .authenticated()

                        // ✅ ACTIVITY
                        .requestMatchers("/api/activity/**")
                        .authenticated()

                        // ✅ ADMIN
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // ✅ DOCTOR
                        .requestMatchers("/api/doctor/**")
                        .hasRole("DOCTOR")

                        // ✅ INTERN
                        .requestMatchers("/api/intern/**")
                        .hasRole("INTERN")

                        // ✅ EVERYTHING ELSE
                        .anyRequest()
                        .authenticated()
                )

                // ✅ JWT FILTER
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ✅ CORS CONFIGURATION
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config =
                new CorsConfiguration();

        // ✅ FRONTEND URLS
        config.setAllowedOriginPatterns(
                List.of(
                        "http://localhost:3000",
                        "https://medical-intern-system-one.onrender.com"
                )
        );

        // ✅ METHODS
        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        // ✅ HEADERS
        config.setAllowedHeaders(
                List.of("*")
        );

        // ✅ EXPOSE JWT
        config.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );

        // ✅ ALLOW COOKIES
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }

    // ✅ PASSWORD ENCODER
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}