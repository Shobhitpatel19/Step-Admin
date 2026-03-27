package com.top.talent.management.security;

import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.constants.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtilService jwtUtilService;
    private final String frontendUri;

    public JwtAuthenticationFilter(JwtUtilService jwtUtilService, @Value("${step.config.frontend-uri}") String frontendUri) {
        this.jwtUtilService = jwtUtilService;
        this.frontendUri = frontendUri;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        log.info("Request received with URI: {}", request.getRequestURI());

        if (authHeader != null && authHeader.startsWith(Constants.BEARER)) {
            log.info("Token found in request header");
            String token = authHeader.substring(7);

            try {
                boolean isValidToken = jwtUtilService.validateToken(token);

                if (isValidToken) {
                    String role = jwtUtilService.extractRole(token);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(jwtUtilService.extractUser(token), null, List.of(new SimpleGrantedAuthority(role)));
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info("User {} authenticated successfully", jwtUtilService.extractEmail(token));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }

            } catch (Exception exception) {
                log.error("Token validation failed for URI: {}. Error: {}", request.getRequestURI(), exception.getMessage(), exception);
                log.info("Token is not valid or expired or user deleted");
                log.info(exception.getMessage());

                response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
                if (exception instanceof UserNotFoundException || exception instanceof AccessDeniedException) {
                    response.getWriter().write(frontendUri + "/unauthorised");
                }else {
                    response.getWriter().write(frontendUri + "/session-expired");
                }

                response.getWriter().flush();
                return;
            }

        }
        filterChain.doFilter(request, response);
    }
}
