package com.project.config.jwt.handler;

import java.io.IOException;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationAccessDeniedHandler implements AccessDeniedHandler {

    private String denied_url;

    public AuthenticationAccessDeniedHandler() {
        this.denied_url = "/accessDenied";
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendRedirect(request.getContextPath() + denied_url);
    }
}