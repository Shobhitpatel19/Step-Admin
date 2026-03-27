package com.top.talent.management.controller;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.EmployeeDTO;
import com.top.talent.management.exception.ApiException;
import com.top.talent.management.exception.EmailException;
import com.top.talent.management.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class EmployeeControllerTest {

    @Mock
    private EmployeeServiceImpl employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = {RoleConstants.PRACTICE, RoleConstants.SUPER_ADMIN, RoleConstants.SUPER_USER})
    void testEmployeeProfile_ValidEmail() {
        String email = "valid_email@epam.com";
        EmployeeDTO employeeDTO = EmployeeDTO.builder().build();
        when(employeeService.getEmployeeProfile(email)).thenReturn(employeeDTO);

        ResponseEntity<EmployeeDTO> response = employeeController.employeeProfile(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeeDTO, response.getBody());
        verify(employeeService, times(1)).getEmployeeProfile(email);
    }

    @Test
    @WithMockUser(roles = {RoleConstants.PRACTICE, RoleConstants.SUPER_ADMIN, RoleConstants.SUPER_USER})
    void testEmployeeProfile_InvalidEmailFormat() {
        String email = "invalid_email"; // Invalid format
        when(employeeService.getEmployeeProfile(email)).thenThrow(new EmailException("Invalid email format"));

        Exception exception = assertThrows(EmailException.class, () ->
                employeeController.employeeProfile(email)
        );

        assertEquals("Invalid email format", exception.getMessage());
        verify(employeeService, times(1)).getEmployeeProfile(email);
    }

    @Test
    @WithMockUser(roles = {RoleConstants.PRACTICE, RoleConstants.SUPER_ADMIN, RoleConstants.SUPER_USER})
    void testEmployeeProfile_EmptyEmail() {
        String email = "";
        when(employeeService.getEmployeeProfile(email)).thenThrow(new EmailException("Email cannot be null or empty"));

        Exception exception = assertThrows(EmailException.class, () ->
                employeeController.employeeProfile(email)
        );

        assertEquals("Email cannot be null or empty", exception.getMessage());
        verify(employeeService, times(1)).getEmployeeProfile(email);
    }

    @Test
    @WithMockUser(roles = {RoleConstants.PRACTICE, RoleConstants.SUPER_ADMIN, RoleConstants.SUPER_USER})
    void testEmployeeProfile_UserNotFound() {
        String email = "notfound@epam.com";
        when(employeeService.getEmployeeProfile(email)).thenThrow(new ApiException(ErrorMessages.NO_DATA_FOUND));

        Exception exception = assertThrows(ApiException.class, () ->
                employeeController.employeeProfile(email)
        );

        assertEquals(ErrorMessages.NO_DATA_FOUND, exception.getMessage());
        verify(employeeService, times(1)).getEmployeeProfile(email);
    }




}
