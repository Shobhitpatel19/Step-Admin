package com.top.talent.management.service.impl;

import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.top.talent.management.constants.Constants.USER_STATUS_INACTIVE;

@Slf4j
@Service
public class JwtUtilService {

    private String secretKey;

    private long expiration;

    private final UserService userService;

    public JwtUtilService(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") long expiration, UserService userService) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.userService = userService;

    }

    public String generateToken(Authentication authentication) {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        UserDTO user = userService.getUser(authentication);
        return Jwts.builder()
                .subject(oidcUser.getEmail())
                .claims(getClaims(oidcUser, user, "ROLE_" + user.getRoleName()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    private Map<String, Object> getClaims(OidcUser oidcUser, UserDTO user, String role) {
        //getting claims from the oidc user and adding roles to it from repository
        Map<String, Object> claims = new HashMap<>(oidcUser.getClaims().entrySet().stream()
                .map(claimName -> Map.entry(claimName.getKey(), claimName.getValue().toString()))
                .collect(HashMap::new, (claimName, claimValue) -> claimName.put(claimValue.getKey(), claimValue.getValue()), HashMap::putAll));
        claims.put("role", role);
        claims.put("isDelegate", user.isDelegate());
        return claims;
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token){
        Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
        String roleFromToken = extractRole(token);
        UserDTO user = userService.getUser(extractEmail(token));
        String roleFromUser = "ROLE_" + user.getRoleName();
        boolean isDelegateFromToken = extractIsDelegate(token);
        boolean isDelegateFromUser = user.isDelegate();
        if(Objects.equals(user.getIsActive(), USER_STATUS_INACTIVE)){
            log.error("User is not active");
            return false;
        }
        if (!roleFromUser.equals(roleFromToken)) {
            log.error("Invalid roles in JWT token");
            return false;
        }
        if(isDelegateFromToken != isDelegateFromUser){
            log.error("Invalid delegate status in JWT token");
            return false;
        }

        return true;
    }


    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractGivenName(String token) {
        return extractClaim(token, claims -> claims.get("given_name", String.class));
    }

    public String extractFamilyName(String token) {
        return extractClaim(token, claims -> claims.get("family_name", String.class));
    }

    private boolean extractIsDelegate(String token) {
        return extractClaim(token, claims -> claims.get("isDelegate", Boolean.class));
    }

    public CustomUserPrincipal extractUser(String token) {
        return new CustomUserPrincipal(extractGivenName(token), extractFamilyName(token), extractEmail(token), extractRole(token), extractIsDelegate(token));
    }

}