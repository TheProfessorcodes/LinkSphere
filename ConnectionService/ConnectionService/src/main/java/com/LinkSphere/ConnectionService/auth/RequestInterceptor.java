package com.LinkSphere.ConnectionService.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if ("/error".equals(request.getRequestURI())) {
            return true;
        }

        String userIdHeader = request.getHeader("X-User-Id");

        System.out.println("X-User-Id received by ConnectionService = " + userIdHeader);

        if (userIdHeader != null && !userIdHeader.isBlank()) {

            try {
                Long userId = Long.valueOf(userIdHeader);
                AuthContextHolder.setCurrentUserId(userId);

            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                @Nullable Exception ex) throws Exception {

        AuthContextHolder.clear();
    }
}
