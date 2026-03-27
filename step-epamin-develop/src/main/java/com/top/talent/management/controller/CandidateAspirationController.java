package com.top.talent.management.controller;

import com.top.talent.management.constants.AspirationPriority;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.AspirationApprovalRequestDTO;
import com.top.talent.management.dto.AspirationDTO;
import com.top.talent.management.dto.AspirationPriorityDTO;
import com.top.talent.management.dto.AspirationResponseDTO;
import com.top.talent.management.dto.SubmitAspirationsRequest;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.CandidateAspirationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.top.talent.management.constants.SwaggerConstants.ACCESS_DENIED;
import static com.top.talent.management.constants.SwaggerConstants.BAD_REQUEST;
import static com.top.talent.management.constants.SwaggerConstants.BEARER_AUTH;
import static com.top.talent.management.constants.SwaggerConstants.CANDIDATE_ASPIRATION_DELETE;
import static com.top.talent.management.constants.SwaggerConstants.CANDIDATE_ASPIRATION_DELETE_DESC_200;
import static com.top.talent.management.constants.SwaggerConstants.CANDIDATE_ASPIRATION_POST;
import static com.top.talent.management.constants.SwaggerConstants.CANDIDATE_ASPIRATION_POST_DESC_200;
import static com.top.talent.management.constants.SwaggerConstants.CANDIDATE_ASPIRATION_PUT;
import static com.top.talent.management.constants.SwaggerConstants.CANDIDATE_ASPIRATION_PUT_DESC_200;
import static com.top.talent.management.constants.SwaggerConstants.GET_CANDIDATE_ASPIRATION_BY_PRIORITY;
import static com.top.talent.management.constants.SwaggerConstants.GET_CANDIDATE_ASPIRATION_BY_PRIORITY_DESC_200;
import static com.top.talent.management.constants.SwaggerConstants.GET_LIST_OF_CANDIDATE_ASPIRATIONS;
import static com.top.talent.management.constants.SwaggerConstants.GET_LIST_OF_CANDIDATE_ASPIRATIONS_DESC_200;
import static com.top.talent.management.constants.SwaggerConstants.SUBMIT_CANDIDATE_ASPIRATION;
import static com.top.talent.management.constants.SwaggerConstants.SUBMIT_CANDIDATE_ASPIRATION_DESC_200;


@RequestMapping("/step/aspirations")
@RequiredArgsConstructor
@RestController
public class CandidateAspirationController {

    private final CandidateAspirationService aspirationService;

    @GetMapping
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_USER + "')")
    @Operation(summary = GET_LIST_OF_CANDIDATE_ASPIRATIONS,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = GET_LIST_OF_CANDIDATE_ASPIRATIONS_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<AspirationResponseDTO> getAspirations(@AuthenticationPrincipal
                                                                    CustomUserPrincipal principal) {
        return ResponseEntity.ok(aspirationService.getAspirations(principal));
    }

    @GetMapping("/{priority}")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_USER + "')")
    @Operation(summary = GET_CANDIDATE_ASPIRATION_BY_PRIORITY,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = GET_CANDIDATE_ASPIRATION_BY_PRIORITY_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<AspirationPriorityDTO> getAspirationByPriority(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable(value = "priority") AspirationPriority priority) {
        return ResponseEntity.ok(aspirationService.getAspirationByPriority(principal, priority));
    }

    @PostMapping
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_USER + "')")
    @Operation(summary = CANDIDATE_ASPIRATION_POST,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = CANDIDATE_ASPIRATION_POST_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<AspirationDTO> createAspiration(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                          @RequestBody AspirationDTO aspirationDTO) {
        return ResponseEntity.ok(aspirationService.saveAspiration(principal, aspirationDTO));
    }

    @PutMapping("/{priority}")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_USER + "')")
    @Operation(summary = CANDIDATE_ASPIRATION_PUT,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = CANDIDATE_ASPIRATION_PUT_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<AspirationDTO> editAspiration(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                        @PathVariable(value = "priority") AspirationPriority priority,
                                                        @RequestBody AspirationDTO aspirationDTO) {
        return ResponseEntity.ok(aspirationService.editAspiration(principal, priority, aspirationDTO));
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_USER + "')")
    @Operation(summary = SUBMIT_CANDIDATE_ASPIRATION,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description =SUBMIT_CANDIDATE_ASPIRATION_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<List<AspirationDTO>> submitAspirations(@AuthenticationPrincipal CustomUserPrincipal principal,  @RequestBody SubmitAspirationsRequest submitRequest){

        return ResponseEntity.ok(aspirationService.submitAspirations(principal, submitRequest));

    }

    @DeleteMapping("/{priority}")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_USER + "')")
    @Operation(summary = CANDIDATE_ASPIRATION_DELETE,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = CANDIDATE_ASPIRATION_DELETE_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<AspirationDTO> deleteAspiration(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                   @PathVariable(value = "priority") AspirationPriority priority) {

        return ResponseEntity.ok(aspirationService.deleteAspiration(principal, priority));
    }

    @GetMapping("/approval")
    @PreAuthorize("hasRole('" + RoleConstants.PRACTICE + "')")
    @Operation(summary = "Get Submitted Aspirations for Approval",
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of submitted aspirations retrieved successfully",
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<List<AspirationResponseDTO>> getSubmittedAspirationsForApproval() {
        List<AspirationResponseDTO> aspirations = aspirationService.getSubmittedAspirationsForApproval();
        return ResponseEntity.ok(aspirations);
    }

    @PostMapping("/{uid}/approve/{aspirationDetailId}")
    @PreAuthorize("hasRole('" + RoleConstants.PRACTICE + "')")
    @Operation(summary = "Approve a Candidate Aspiration",
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Aspiration approved successfully",
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "404", description = "Aspiration not found",
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<Void> approveAspiration(
            @PathVariable(value = "uid") Long uid,
            @PathVariable(value = "aspirationDetailId") Long aspirationDetailId,
            @RequestBody AspirationApprovalRequestDTO approvalRequest) {
        aspirationService.approveAspiration(uid, aspirationDetailId, approvalRequest);
        return ResponseEntity.ok().build();
    }

}

