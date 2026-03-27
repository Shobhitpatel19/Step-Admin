package com.top.talent.management.utils;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.User;
import com.top.talent.management.security.CustomUserPrincipal;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

    public static Authentication getMockAuthenticationWithSecurity(String role) {
        CustomUserPrincipal principal = new CustomUserPrincipal("John", "Doe", "test_user@epam.com", role, false);
        Authentication authentication = new TestingAuthenticationToken(principal, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        return authentication;
    }

    public static Authentication getMockAuthentication() {
        CustomUserPrincipal principal = new CustomUserPrincipal("John", "Doe", "test_user@epam.com", RoleConstants.ROLE_USER, false);
        return new TestingAuthenticationToken(principal, null, Collections.singletonList(new SimpleGrantedAuthority(RoleConstants.ROLE_USER)));
    }

    public static Authentication getMockAuthentication(String role) {
        CustomUserPrincipal principal = new CustomUserPrincipal("John", "Doe", "test_user@epam.com", role, false);
        return new TestingAuthenticationToken(principal, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
    }

    public static UserDTO getUserDTO() {
        return UserDTO.builder()
                .email("test_user@epam.com")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    public static UserDTO getUserDTOWithRole() {
        return UserDTO.builder()
                .email("test_user@epam.com")
                .firstName("John")
                .lastName("Doe")
                .roleName("P")
                .build();
    }


    public static Role getRole() {
        Role role = new Role();
        role.setName("P");
        role.setCreated(LocalDateTime.now());
        role.setCreatedBy("admin");
        role.setLastUpdated(LocalDateTime.now());
        role.setLastUpdatedBy("admin");
        return role;
    }

    public static User getUser(Role role) {
        User user = new User();
        user.setUuid(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setStatus("Active");
        user.setPractice("Software Development");
        user.setRole(role);
        user.setCreated(LocalDateTime.now());
        user.setCreatedBy("admin");
        user.setLastUpdated(LocalDateTime.now());
        user.setLastUpdatedBy("admin");
        return user;
    }
}
