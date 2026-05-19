package com.system.librarymanagmentsystem.config;

import com.system.librarymanagmentsystem.handlers.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtFilter implements Filter {

    private final JwtUtils jwtUtils;

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/books/all"
    );

    public JwtFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;
        String path = httpReq.getRequestURI();

        String origin = httpReq.getHeader("Origin");
        if (origin != null) {
            httpRes.setHeader("Access-Control-Allow-Origin", origin);
            httpRes.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpRes.setHeader("Access-Control-Allow-Headers", "*");
        }

        if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (PUBLIC_PATHS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpReq.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpRes.setContentType("application/json");
            httpRes.getWriter().write("{\"success\":false,\"message\":\"Missing or invalid Authorization header\"}");
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.isValid(token)) {
            httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpRes.setContentType("application/json");
            httpRes.getWriter().write("{\"success\":false,\"message\":\"Invalid or expired token\"}");
            return;
        }

        httpReq.setAttribute("userId", jwtUtils.getUserId(token));
        httpReq.setAttribute("role", jwtUtils.getRole(token));

        chain.doFilter(request, response);
    }
}
