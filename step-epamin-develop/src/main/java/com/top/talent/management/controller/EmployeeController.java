package com.top.talent.management.controller;

import com.top.talent.management.dto.EmployeeDTO;
import com.top.talent.management.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.top.talent.management.constants.ErrorMessages.ACCESS_DENIED;
import static com.top.talent.management.constants.RoleConstants.HAS_ANY_ROLE_SU_SA_P_U;
import static com.top.talent.management.constants.SwaggerConstants.BAD_REQUEST;
import static com.top.talent.management.constants.SwaggerConstants.BEARER_AUTH;
import static com.top.talent.management.constants.SwaggerConstants.GET_EMPLOYEE_PROFILE_BY_EMAIL;
import static com.top.talent.management.constants.SwaggerConstants.GET_EMPLOYEE_PROFILE_BY_EMAIL_DESC_200;


@Slf4j
@RequestMapping("/step")
@RequiredArgsConstructor
@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/employee-profile")
    @PreAuthorize(HAS_ANY_ROLE_SU_SA_P_U)
    @Operation(summary = GET_EMPLOYEE_PROFILE_BY_EMAIL,
            security = @SecurityRequirement(name = BEARER_AUTH),
            responses = {
                    @ApiResponse(responseCode = "200", description = GET_EMPLOYEE_PROFILE_BY_EMAIL_DESC_200,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST,
                            content = {@Content(mediaType = "application/json")}),
                    @ApiResponse(responseCode = "403", description = ACCESS_DENIED,
                            content = {@Content(mediaType = "application/json")})
            })
    public ResponseEntity<EmployeeDTO> employeeProfile(@RequestParam String email){
        log.info("Request to get employee profile by email");
        return ResponseEntity.ok(employeeService.getEmployeeProfile(email));

    }
}
