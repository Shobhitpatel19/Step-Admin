package com.top.talent.management.security;

import com.top.talent.management.service.impl.JwtUtilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtilService jwtUtilService;
    private final String frontendUri;

    public CustomAuthenticationSuccessHandler(JwtUtilService jwtUtilService, @Value("${step.config.frontend-uri}") String frontendUri) {
        this.jwtUtilService = jwtUtilService;
        this.frontendUri = frontendUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try{
            String token = jwtUtilService.generateToken(authentication);
            log.info("User {} logged in successfully", authentication.getPrincipal());
            response.sendRedirect(frontendUri + "/?token=" + token);
        } catch (Exception exception){
            log.error(exception.getMessage(),exception);
            response.sendRedirect(frontendUri + "/unauthorised");
        }
    }
}

