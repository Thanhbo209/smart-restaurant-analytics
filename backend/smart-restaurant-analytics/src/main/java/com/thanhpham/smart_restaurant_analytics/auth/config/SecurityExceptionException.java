package com.thanhpham.smart_restaurant_analytics.auth.config;

import java.io.IOException;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanhpham.smart_restaurant_analytics.common.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityExceptionException implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectProvider<ObjectMapper> objectMapperProvider;

    // 401 — no token or invalid token
    @Override

    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex) throws IOException {
        writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                "Authentication required. Provide a valid Bearer token.");
    }

    // 403 — valid token but wrong role
    @Override

    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex) throws IOException {
        writeError(response, HttpServletResponse.SC_FORBIDDEN,
                "Access denied. You do not have permission to perform this action.");
    }

    private void writeError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapperProvider.getObject().writeValue(response.getWriter(), ApiResponse.error(message));
    }
}
