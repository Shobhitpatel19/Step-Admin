package com.top.talent.management.controller;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.ExcelFileDetailsDTO;
import com.top.talent.management.dto.MasterDataResponseDTO;
import com.top.talent.management.dto.TalentProfileDTO;
import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.CulturalScoreService;
import com.top.talent.management.service.EngXExtraMileRatingService;
import com.top.talent.management.service.ExcelFileDetailsService;
import com.top.talent.management.service.MasterDataService;
import com.top.talent.management.service.TopTalentEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.top.talent.management.constants.SwaggerConstants.ACCESS_DENIED;
import static com.top.talent.management.constants.SwaggerConstants.BAD_REQUEST;
import static com.top.talent.management.constants.SwaggerConstants.BEARER_AUTH;
import static com.top.talent.management.constants.SwaggerConstants.CULTURAL_SCORE_POST;
import static com.top.talent.management.constants.SwaggerConstants.CULTURAL_SCORE_POST_DESC_200;
import static com.top.talent.management.constants.SwaggerConstants.ENGX_EXTRAMILE_POST;
import static com.top.talent.management.constants.SwaggerConstants.ENGX_EXTRAMILE_POST_DESC_200;
import static com.top.talent.management.constants.SwaggerConstants.GET_EMPLOYEE_DATA;
import static com.top.talent.management.constants.SwaggerConstants.GET_EMPLOYEE_DATA_DESC_200;
import static com.top.talent.management.constants.SwaggerConstants.TOP_TALENT_EMPLOYEE_UPLOAD;
import static com.top.talent.management.constants.SwaggerConstants.TOP_TALENT_EMPLOYEE_UPLOAD_DESC_200;

@RequiredArgsConstructor
@RestController
@RequestMapping("/step")
@Slf4j
public class TopTalentEmployeeController {

    private final TopTalentEmployeeService topTalentEmployeeService;

    private final EngXExtraMileRatingService engXExtraMileRatingService;

    private final CulturalScoreService culturalScoreService;

    private final MasterDataService masterDataService;

    private final ExcelFileDetailsService excelNameService;


    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SUPER_ADMIN + "')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = TOP_TALENT_EMPLOYEE_UPLOAD,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = TOP_TALENT_EMPLOYEE_UPLOAD_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<List<TopTalentEmployeeDTO>> upload(@RequestPart("file") MultipartFile file, @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("Request to upload excel is received");
        return ResponseEntity.ok(topTalentEmployeeService.parseAndSaveExcel(file, customUserPrincipal));
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('" + RoleConstants.ROLE_SUPER_ADMIN + "', '" + RoleConstants.ROLE_SUPER_USER + "', '" + RoleConstants.ROLE_PRACTICE + "')")
    @Operation(summary = GET_EMPLOYEE_DATA,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = GET_EMPLOYEE_DATA_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<List<TopTalentEmployeeDTO>> getEmployeesData() {
        log.info("Request to get Employees");
        List<TopTalentEmployeeDTO> topTalentEmployeeDTOS = topTalentEmployeeService.getAllEmployeeDataByLatestVersion();
        return ResponseEntity.ok(topTalentEmployeeDTOS);
    }

    @GetMapping("/employees/{uid}")
    @PreAuthorize("hasAnyRole('" + RoleConstants.ROLE_SUPER_ADMIN + "', '" + RoleConstants.ROLE_SUPER_USER + "', '" + RoleConstants.ROLE_PRACTICE + "')")
    @Operation(summary = GET_EMPLOYEE_DATA,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = GET_EMPLOYEE_DATA_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<List<TopTalentEmployeeDTO>> getAllEmployeesByUid(@PathVariable(name = "uid") Long uid) {
        log.info("Request to get Employees by UID");
        return ResponseEntity.ok(topTalentEmployeeService.getAllEmployeeDataByUid(uid));
    }

    @GetMapping("/talent-profile/{uid}")
    @PreAuthorize("hasAnyRole('" + RoleConstants.ROLE_SUPER_ADMIN + "', '" + RoleConstants.ROLE_SUPER_USER + "', '" + RoleConstants.ROLE_PRACTICE + "')")
    @Operation(summary = GET_EMPLOYEE_DATA,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
            @ApiResponse(responseCode = "200", description = GET_EMPLOYEE_DATA_DESC_200,
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                    content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<List<TalentProfileDTO>> getEmployeeDataForTalentProfile(@PathVariable Long uid) {
        log.info("Request to get Employees for Talent Profile");
        return ResponseEntity.ok(topTalentEmployeeService.getEmployeeDataForTalentProfile(uid));
    }

    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SUPER_ADMIN + "')")
    @PostMapping(value = "/engx-extra-mile-rating/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = ENGX_EXTRAMILE_POST,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = ENGX_EXTRAMILE_POST_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<List<TopTalentEmployeeDTO>> uploadEngXExtraMileRating(@RequestPart("file") MultipartFile file, @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
        log.info("Request to upload EngX Extra Mile Rating excel is received");
        return ResponseEntity.ok(engXExtraMileRatingService.parseAndSaveExcel(file, customUserPrincipal));
    }

    @PostMapping(value = "/cultural-score/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('"+RoleConstants.ROLE_SUPER_ADMIN+"')")
    @Operation(summary = CULTURAL_SCORE_POST,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
            @ApiResponse(responseCode = "200", description = CULTURAL_SCORE_POST_DESC_200,
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                    content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<List<TopTalentEmployeeDTO>> culturalScoreUpload(@RequestPart("file") MultipartFile file, @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal) {
       log.info("Request to upload Cultural Score excel is received");
        return ResponseEntity.ok(culturalScoreService.parseAndSaveCulturalScore(file,customUserPrincipal));
    }

    @GetMapping(value = "/master-data")
    @PreAuthorize("hasAnyRole('"+RoleConstants.ROLE_SUPER_ADMIN+"')")
    @Operation(summary ="View master data",
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched master data",
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })

    public MasterDataResponseDTO viewMasterData(@RequestParam(required = false) String fileName){

        return masterDataService.viewMasterData(fileName);

    }

    @PostMapping(value = "/save-master-data")
    @PreAuthorize("hasAnyRole('"+RoleConstants.ROLE_SUPER_ADMIN+"')")
    @Operation(summary ="save master data",
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully saved master data",
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })

    public MasterDataResponseDTO saveFilteredMasterData(@RequestParam SubmissionStatus submissionStatus, @RequestBody List<Long> uidS, @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal){

        return masterDataService.saveEmployeesOfExcelVersion( submissionStatus,uidS,customUserPrincipal);

    }

    @GetMapping(value = "/user-profile")
    @PreAuthorize("hasAnyRole('"+RoleConstants.ROLE_SUPER_ADMIN+"')")
    @Operation(summary ="user profile",
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = "successfully fetched user profile",
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })

    public ResponseEntity<Map<Long, UserProfile>> getUserProfile(){

        return ResponseEntity.ok(masterDataService.getUserProfile());

    }

    @GetMapping("/file-details")
    public ResponseEntity<ExcelFileDetailsDTO> getFileDetails(@RequestParam String excelType) {
        return ResponseEntity.ok(excelNameService.generateExcelFile(excelType));
    }
}