package com.top.talent.management.controller;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SwaggerConstants;
import com.top.talent.management.dto.UserResponseDTO;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.SuperAdminService;
import com.top.talent.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/step/super-admin")
@Slf4j
public class SuperAdminController {

    private final UserService userService;

    private final SuperAdminService superAdminService;

    @PostMapping("/grant-access")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = "All STEP users", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully granted access to these users",
                    content = {@Content(mediaType = "application/json")}),})

    public ResponseEntity<List<UserDTO>> grantStepAccessToUserRole() {
        log.info("Request to grant access to users.");
        return ResponseEntity.ok(superAdminService.grantAccessToUserRole());
    }

    @GetMapping("/access-privileges")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = SwaggerConstants.SUPER_ADMIN_GET_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.SUPER_ADMIN_GET_DESC_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)})
            })
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@RequestParam String roleName, @RequestParam Optional<String> status)
    {
        log.info("Request to get all users by role: {}", roleName);
        return ResponseEntity.ok(userService.getUsersByRole(roleName, status));
    }

    @PostMapping("/access-privileges/add-user")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = SwaggerConstants.SUPER_ADMIN_POST_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.SUPER_ADMIN_POST_DESC_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)})
            })
    public ResponseEntity<UserResponseDTO> addUser(@RequestBody UserDTO requestDTO, @AuthenticationPrincipal CustomUserPrincipal userPrincipal)
    {
        log.info("Request to add user with uid {} and role {}", requestDTO.getUuid(), requestDTO.getRoleName());

        return ResponseEntity.ok(userService.addUser(requestDTO, userPrincipal.getFullName()));
    }

    @DeleteMapping("/access-privileges/deactivate-user/{uid}")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = SwaggerConstants.SUPER_ADMIN_DELETE_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.SUPER_ADMIN_DELETE_DESC_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)})
            })
    public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable Long uid, @AuthenticationPrincipal CustomUserPrincipal userPrincipal)
    {
        log.info("Request to deactivate user with uid {}", uid);

        return ResponseEntity.ok(userService.deactivateUser(uid, userPrincipal.getFullName()));
    }

}
