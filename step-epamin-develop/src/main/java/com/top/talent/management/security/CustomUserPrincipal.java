package com.top.talent.management.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

@Data
@AllArgsConstructor
public class CustomUserPrincipal implements AuthenticatedPrincipal {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean isDelegate;

    @Override
    public String getName() {
        return email;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public static CustomUserPrincipal getLoggedInUser(){
        return (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String getLoggedInUserEmail(){
        return getLoggedInUser().getEmail();
    }

}
