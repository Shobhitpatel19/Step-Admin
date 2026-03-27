package com.top.talent.management.exception;

import com.top.talent.management.constants.ErrorMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        String requestURI = request.getRequestURI();
        Authentication token = (Authentication) request.getUserPrincipal();
        AuthenticatedPrincipal userPrincipal = (AuthenticatedPrincipal) token.getPrincipal();
        String email = userPrincipal.getName();

        log.error(ErrorMessages.URI_ACCESS_DENIED, email, requestURI);

        response.sendError(HttpServletResponse.SC_FORBIDDEN, ErrorMessages.ACCESS_DENIED);
    }
}