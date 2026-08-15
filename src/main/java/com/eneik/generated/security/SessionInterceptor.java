package com.eneik.generated.security;

import com.eneik.generated.dto.ErrorResponse;
import com.eneik.generated.service.SessionAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Clock;
import java.util.Collections;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionAuthService sessionAuthService;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Autowired
    public SessionInterceptor(SessionAuthService sessionAuthService, ObjectMapper objectMapper) {
        this(sessionAuthService, objectMapper, Clock.systemUTC());
    }

    public SessionInterceptor(SessionAuthService sessionAuthService, ObjectMapper objectMapper, Clock clock) {
        this.sessionAuthService = sessionAuthService;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Protect knowledge base management functions (POST/PUT/DELETE/PATCH under /api/v1/materials)
        if (path.startsWith("/api/v1/materials") && isManagementMethod(method)) {
            String token = extractToken(request);
            if (token == null || sessionAuthService.validateSession(token).isEmpty()) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                ErrorResponse error = new ErrorResponse(
                        "UNAUTHORIZED",
                        "Missing or invalid session context. Authentication token is required for management functions.",
                        clock.instant(),
                        Collections.singletonList("Authentication required for endpoint: " + path)
                );
                response.getWriter().write(objectMapper.writeValueAsString(error));
                return false;
            }
        }
        return true;
    }

    private boolean isManagementMethod(String method) {
        return "POST".equalsIgnoreCase(method) ||
               "PUT".equalsIgnoreCase(method) ||
               "DELETE".equalsIgnoreCase(method) ||
               "PATCH".equalsIgnoreCase(method);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.trim().isEmpty()) {
            return authHeader;
        }
        String sessionHeader = request.getHeader("X-Session-Token");
        if (sessionHeader != null && !sessionHeader.trim().isEmpty()) {
            return sessionHeader;
        }
        return null;
    }
}
