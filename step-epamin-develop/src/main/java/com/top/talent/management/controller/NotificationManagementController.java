package com.top.talent.management.controller;


import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.NotificationRequestDTO;
import com.top.talent.management.dto.UserNotificationResponseDTO;
import com.top.talent.management.service.NotificationManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.top.talent.management.constants.ErrorMessages.ACCESS_DENIED;
import static com.top.talent.management.constants.SwaggerConstants.BAD_REQUEST;
import static com.top.talent.management.constants.SwaggerConstants.BEARER_AUTH;
import static com.top.talent.management.constants.SwaggerConstants.GET_USER_WITH_FEATURES;
import static com.top.talent.management.constants.SwaggerConstants.GET_USER_WITH_FEATURES_200_DESC;
import static com.top.talent.management.constants.SwaggerConstants.TOGGLE_ALL_NOTIFICATIONS;
import static com.top.talent.management.constants.SwaggerConstants.TOGGLE_ALL_NOTIFICATIONS_200_DESC;
import static com.top.talent.management.constants.SwaggerConstants.TOGGLE_NOTIFICATION;
import static com.top.talent.management.constants.SwaggerConstants.TOGGLE_NOTIFICATION_200_DESC;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/step/notifications")
public class NotificationManagementController {

    private final NotificationManagementService notificationManagementService;


    @GetMapping()
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SUPER_ADMIN + "')")
    @Operation(summary = GET_USER_WITH_FEATURES,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description =GET_USER_WITH_FEATURES_200_DESC ,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<List<UserNotificationResponseDTO>> getPracticeHeadsWithFeatures() {
        List<UserNotificationResponseDTO> users = notificationManagementService.getAllUsersWithNotificationpreferences();
        log.info("Request to get all users with features");
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SUPER_ADMIN + "')")
    @Operation(summary =TOGGLE_ALL_NOTIFICATIONS,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = TOGGLE_ALL_NOTIFICATIONS_200_DESC,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<UserNotificationResponseDTO> toggleAllPracticeHeadNotifications(@PathVariable Long userId,
                                                                                          @RequestParam Boolean enable) {
        log.info("Request to toggle all notifications for user with id: {}", userId);

        return ResponseEntity.ok( notificationManagementService.toggleAllNotificationsForUser(userId, enable));
    }
    @PatchMapping()
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SUPER_ADMIN + "')")
    @Operation(summary =TOGGLE_NOTIFICATION  ,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = TOGGLE_NOTIFICATION_200_DESC,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<UserNotificationResponseDTO> toggleNotification(@Valid  @RequestBody NotificationRequestDTO requestDTO) {
        log.info("Request to toggle notification for user with id: {} and category with id: {}, enable: {}",
                requestDTO.getUserId(), requestDTO.getCategoryId(), requestDTO.getEnable());

        return ResponseEntity.ok(notificationManagementService.updateCategoryNotificationStatusForUser(requestDTO));
    }
}
