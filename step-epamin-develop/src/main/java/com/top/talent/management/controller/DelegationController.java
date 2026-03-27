package com.top.talent.management.controller;

import com.top.talent.management.constants.SwaggerConstants;
import com.top.talent.management.dto.PracticeDelegationDTO;
import com.top.talent.management.entity.Delegation;
import com.top.talent.management.service.PracticeDelegationFeatureService;
import com.top.talent.management.service.DelegationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.top.talent.management.constants.RoleConstants.HAS_ANY_ROLE_SA_P;


@RequiredArgsConstructor
@RestController
@RequestMapping("/step")
@Slf4j
public class DelegationController {
    private final DelegationService delegationService;
    private final PracticeDelegationFeatureService practiceDelegationFeatureService;

    @PostMapping("delegate")
    @PreAuthorize(HAS_ANY_ROLE_SA_P)
    @Operation(summary = SwaggerConstants.PRACTICE_DELEGATION_CREATE_DELEGATE_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_DELEGATION_CREATE_DELEGATE_SUMMARY_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
            })
    public ResponseEntity<PracticeDelegationDTO> createPracticeDelegate(@RequestBody Delegation delegation, @RequestParam String competency){
        return ResponseEntity.ok(delegationService.createPracticeDelegate(delegation, competency));
    }

    @GetMapping("delegate")
    @PreAuthorize(HAS_ANY_ROLE_SA_P)
    @Operation(summary = SwaggerConstants.PRACTICE_DELEGATION_GET_DELEGATE_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_DELEGATION_GET_DELEGATE_SUMMARY_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
            })
    public ResponseEntity<PracticeDelegationDTO> getPracticeDelegate(@RequestParam String competency) {
        return ResponseEntity.ok(delegationService.getPracticeDelegate(competency));
    }

    @DeleteMapping("delegate")
    @PreAuthorize(HAS_ANY_ROLE_SA_P)
    @Operation(summary = SwaggerConstants.PRACTICE_DELEGATION_DELETE_DELEGATE_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_DELEGATION_DELETE_DELEGATE_SUMMARY_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
            })
    public ResponseEntity<PracticeDelegationDTO> deletePracticeDelegate(@RequestParam String competency){
        return ResponseEntity.ok(delegationService.deletePracticeDelegate(competency));
    }

    @GetMapping("get-available-practice-features")
    @PreAuthorize(HAS_ANY_ROLE_SA_P)
    @Operation(summary = SwaggerConstants.PRACTICE_DELEGATION_GET_AVAILABLE_FEATURE_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_DELEGATION_GET_AVAILABLE_FEATURE_SUMMARY_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
            })
    public ResponseEntity<List<String>> getAvailablePracticeFeaturesToDelegate(){
        return ResponseEntity.ok(practiceDelegationFeatureService.getAllFeaturesName());
    }


}
