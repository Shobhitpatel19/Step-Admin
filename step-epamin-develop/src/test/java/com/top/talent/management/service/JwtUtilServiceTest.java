package com.top.talent.management.service;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.JwtUtilService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Map;

import static com.top.talent.management.utils.TestUtils.getUserDTOWithRole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;


    private JwtUtilService jwtUtilService;


    @BeforeEach
    public void setUp() {
        String secretKey = "FDP2SqwbW5bU2UmJIfFnw89v+vCe5ZmpUye3My6PgcA=";
        long expiration = 3600000;
        jwtUtilService = new JwtUtilService(secretKey, expiration, userService);
    }

    @Test
    void testGenerateToken() {
        mockService();
        UserDTO user = UserDTO.builder()
                .roleName("P")
                        .isDelegate(false).build();

        when(userService.getUser("test_user@epam.com")).thenReturn(user);

        String token = jwtUtilService.generateToken(authentication);

        assertNotNull(token, "Token should not be null");
        assertTrue(jwtUtilService.validateToken(token), "Token should be valid");
    }

    @Test
    void testTokenContainsCorrectClaims() {
        mockService();
        String token = jwtUtilService.generateToken(authentication);

        assertEquals("test_user@epam.com", jwtUtilService.extractEmail(token), "Email should match");
        assertEquals(RoleConstants.ROLE_PRACTICE, jwtUtilService.extractRole(token), "Role should match");
        assertEquals("John", jwtUtilService.extractGivenName(token), "Given name should match");
        assertEquals("Doe", jwtUtilService.extractFamilyName(token), "Family name should match");
    }


    @Test
    void testValidateTokenWithMismatchedRole() {
        mockService();
        UserDTO user = UserDTO.builder()
                .roleName("SA")
                .isDelegate(false).build();
        when(userService.getUser("test_user@epam.com")).thenReturn(user);

        String tokenWithWrongRole = jwtUtilService.generateToken(authentication);

        assertFalse(jwtUtilService.validateToken(tokenWithWrongRole), "Token with mismatched role should be invalid");
    }

    @Test
    void testValidateTokenWithDelegateWrong() {
        mockService();
        UserDTO user = UserDTO.builder()
                .roleName("P")
                .isDelegate(true).build();
        when(userService.getUser("test_user@epam.com")).thenReturn(user);

        String tokenWithWrongRole = jwtUtilService.generateToken(authentication);

        assertFalse(jwtUtilService.validateToken(tokenWithWrongRole), "Token with mismatched delegate should be invalid");
    }

    @Test
    void testValidateTokenWithUserInactive() {
        mockService();
        UserDTO user = UserDTO.builder()
                .roleName("P")
                .isDelegate(false).
                isActive("Inactive")
            .build();
        when(userService.getUser("test_user@epam.com")).thenReturn(user);

        String tokenWithWrongRole = jwtUtilService.generateToken(authentication);

        assertFalse(jwtUtilService.validateToken(tokenWithWrongRole), "user is inactive");
    }

    @Test
    void testMalformedToken() {
        String malformedToken = "malformed.token.content";

        assertThrows(MalformedJwtException.class, () -> jwtUtilService.validateToken(malformedToken), "Malformed token should throw exception");
    }

    @Test
    void testCustomPrincipalExtraction() {
        mockService();
        String token = jwtUtilService.generateToken(authentication);

        CustomUserPrincipal principal = jwtUtilService.extractUser(token);

        assertEquals("John Doe", principal.getFullName(), "Full name should match");
        assertEquals("test_user@epam.com", principal.getEmail(), "Email should match");
        assertEquals(RoleConstants.ROLE_PRACTICE, principal.getRole(), "Role should match");
    }


    @Test
    void testInvalidSignature() {
        String otherKey = "ZORJfeKlzo/ZKVW6iW1Dgy9ANrpB6zMLZo8E5kfu/+U=";

        System.out.println(otherKey);
        String tokenWithInvalidSignature = Jwts.builder()
                .subject("test_user@epam.com")
                .signWith(Keys.hmacShaKeyFor(otherKey.getBytes()))
                .compact();

        assertThrows(SignatureException.class, () -> jwtUtilService.validateToken(tokenWithInvalidSignature), "Invalid signature should throw exception");
    }

    private void mockService() {
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getClaims()).thenReturn(Map.of("given_name", "John", "family_name", "Doe", "email", "test_user@epam.com"));
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn("test_user@epam.com");
        when(userService.getUser(authentication)).thenReturn(getUserDTOWithRole());
    }
}
