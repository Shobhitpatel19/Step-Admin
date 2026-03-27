package com.top.talent.management.controller;

import com.top.talent.management.constants.SwaggerConstants;
import com.top.talent.management.dto.PracticeDelegationFeatureDTO;
import com.top.talent.management.service.PracticeDelegateUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.top.talent.management.constants.RoleConstants.HAS_ROLE_PRACTICE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/step")
@Slf4j
public class PracticeDelegateUserController {
    private final PracticeDelegateUserService practiceDelegateUserService;

    @GetMapping("get-delegated-features")
    @PreAuthorize(HAS_ROLE_PRACTICE)
    @Operation(summary = SwaggerConstants.PRACTICE_DELEGATE_USER_GET_DELEGATED_FEATURE_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_DELEGATE_USER_GET_DELEGATED_FEATURE_SUMMARY_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
            })
    public ResponseEntity<List<PracticeDelegationFeatureDTO>> getDelegatedFeatures(){
        return ResponseEntity.ok(practiceDelegateUserService.getDelegatedFeatures());
    }

    @GetMapping("is-approval-required")
    @PreAuthorize(HAS_ROLE_PRACTICE)
    @Operation(summary = SwaggerConstants.PRACTICE_DELEGATE_USER_IS_APPROVAL_REQUIRED_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_DELEGATE_USER_IS_APPROVAL_REQUIRED_SUMMARY_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
            })
    public ResponseEntity<Boolean> getIsApprovalRequired(){
        return ResponseEntity.ok(practiceDelegateUserService.isApprovalRequired());
    }

    @GetMapping("has-access-to-feature")
    @PreAuthorize(HAS_ROLE_PRACTICE)
    @Operation(summary = SwaggerConstants.PRACTICE_DELEGATE_USER_HAS_FEATURE_ACCESS_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_DELEGATE_USER_HAS_FEATURE_ACCESS_SUMMARY_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
            })
    public ResponseEntity<Boolean> getHasAccessToFeature(@PathParam("featureName") String featureName){
        return ResponseEntity.ok(practiceDelegateUserService.hasAccessToFeature(featureName));
    }

}
