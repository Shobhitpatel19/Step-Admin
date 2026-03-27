package com.top.talent.management.controller;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.constants.SwaggerConstants;
import com.top.talent.management.dto.EmployeeRatingRequestDTO;
import com.top.talent.management.dto.EmployeeRatingResponseDTO;
import com.top.talent.management.dto.PracticeRatingResponseDTO;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.PracticeRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/step/practice-rating")
public class PracticeRatingController {

    private final PracticeRatingService practiceRatingService;

    @GetMapping("/competencies")
    @PreAuthorize(RoleConstants.HAS_ROLE_SUPER_ADMIN)
    @Operation(summary = SwaggerConstants.PRACTICE_RATING_GET_COMPETENCY_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_RATING_GET_COMPETENCY_DESC_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "401", description = SwaggerConstants.MEDIA_TYPE_JSON,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)})
            })
    public ResponseEntity<List<String>> getCompetencies() {
        log.info("Request to get all competencies");

        return ResponseEntity.ok(practiceRatingService.getCompetencies());
    }

    @GetMapping("/employees")
    @PreAuthorize(RoleConstants.HAS_ANY_ROLE_SA_P)
    @Operation(summary = SwaggerConstants.PRACTICE_RATING_LIST_GET_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_RATING_LIST_GET_DESC_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "401", description = SwaggerConstants.PRACTICE_RATING_POST_DESC_401,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)})
            })
    public ResponseEntity<PracticeRatingResponseDTO> getEmployees(@AuthenticationPrincipal CustomUserPrincipal userPrincipal,
                                                                  @RequestParam(defaultValue = Constants.DEFAULT_COMPETENCY)
                                                                  String competency) {
        log.info("Request to get employees from same practice as Practice Head");

        return ResponseEntity.ok(practiceRatingService.getCandidates(userPrincipal.getEmail(), competency));
    }

    @PreAuthorize(RoleConstants.HAS_ANY_ROLE_SA_P)
    @GetMapping("/employees/get-rating/{uid}")
    @Operation(summary = SwaggerConstants.PRACTICE_RATING_GET_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_RATING_GET_DESC_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "401", description = SwaggerConstants.PRACTICE_RATING_POST_DESC_401,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)})
            })
    public ResponseEntity<EmployeeRatingResponseDTO> getEmployeeRating(@PathVariable(name = "uid") Long uid, @AuthenticationPrincipal CustomUserPrincipal userPrincipal)
    {
        log.info("Request to get employee ratings for uid {}", uid);

        return ResponseEntity.ok(practiceRatingService.getEmployeeRating(uid, userPrincipal));
    }

    @PreAuthorize(RoleConstants.HAS_ANY_ROLE_SA_P)
    @PostMapping("/employees/save-rating/{uid}")
    @Operation(summary = SwaggerConstants.PRACTICE_RATING_POST_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = SwaggerConstants.PRACTICE_RATING_POST_DESC_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400", description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403", description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "401", description = SwaggerConstants.PRACTICE_RATING_POST_DESC_401,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)})
            })
    public ResponseEntity<EmployeeRatingResponseDTO> updateEmployeeRatings(@PathVariable(name = "uid") Long uid, @NotNull @RequestParam SubmissionStatus submissionStatus, @RequestBody EmployeeRatingRequestDTO employeeRatingRequestDTO, @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        log.info("Request to save employee ratings for uid {} with submission status {}", uid, submissionStatus);

        return ResponseEntity.ok(practiceRatingService.saveEmployeeRatings(uid, submissionStatus,employeeRatingRequestDTO, userPrincipal));
    }

    @PreAuthorize(RoleConstants.HAS_ROLE_PRACTICE)
    @PostMapping("/employees/approve-all")
    @Operation(
            summary = SwaggerConstants.PRACTICE_RATING_POST_APPROVE_ALL_SUMMARY,
            security = @SecurityRequirement(name = SwaggerConstants.BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = SwaggerConstants.PRACTICE_RATING_POST_APPROVE_ALL_200,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "400",
                            description = SwaggerConstants.BAD_REQUEST,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "403",
                            description = SwaggerConstants.ACCESS_DENIED,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)}),
                    @ApiResponse(responseCode = "401",
                            description = SwaggerConstants.PRACTICE_RATING_DESC_401,
                            content = {@Content(mediaType = SwaggerConstants.MEDIA_TYPE_JSON)})
            }
    )
    public ResponseEntity<Object> approveAllEmployeesRatings() {
        return ResponseEntity.ok(practiceRatingService.approveAllEmployeesRatings());
    }

}
