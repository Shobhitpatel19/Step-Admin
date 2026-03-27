package com.top.talent.management.controller;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.FutureSkillPracticeDTO;
import com.top.talent.management.dto.FutureSkillRequestListDTO;
import com.top.talent.management.dto.FutureSkillResponseDTO;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.FutureSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.top.talent.management.constants.SwaggerConstants.ACCESS_DENIED;
import static com.top.talent.management.constants.SwaggerConstants.BAD_REQUEST;
import static com.top.talent.management.constants.SwaggerConstants.BEARER_AUTH;

@RestController
@RequestMapping("/step")
@RequiredArgsConstructor
@Slf4j
public class FutureSkillController {

    private final FutureSkillService futureSkillService;

    @PreAuthorize(RoleConstants.HAS_ROLE_SUPER_ADMIN)
    @GetMapping("/practices/future-skills")
    @Operation(summary = "Get All Practices with Future Skills Statuses",
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved future skills",
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = "Access Denied")
            })
    public ResponseEntity<List<FutureSkillPracticeDTO>> getPracticeDetailsAndSubmissionStatuses() {
        List<FutureSkillPracticeDTO> response = futureSkillService.getPracticeDetailsAndSubmissionStatus();

        return ResponseEntity.ok(response);
    }


    @PreAuthorize(RoleConstants.HAS_ANY_ROLE_SU_SA_P_U)
    @GetMapping("/future-skills")
    @Operation(summary = "Get Future Skills",
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved future skills",
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<FutureSkillResponseDTO> getFutureSkills(@AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("Request to get future skills for Practice Head: {}", customUserPrincipal.getFullName());
        FutureSkillResponseDTO response = futureSkillService.getFutureSkill(customUserPrincipal);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('" + RoleConstants.ROLE_PRACTICE + "')")
    @PostMapping("/future-skills")
    @Operation(summary = "Save Future Skills",
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Future skills saved successfully",
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<String> saveFutureSkills(@RequestBody FutureSkillRequestListDTO futureSkillRequestListDTO,
                                                   @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("Request to save future skills for Practice Head: {}", customUserPrincipal.getFullName());
        String response = futureSkillService.saveFutureSkill(customUserPrincipal, futureSkillRequestListDTO);
        return ResponseEntity.ok(response);
    }
}