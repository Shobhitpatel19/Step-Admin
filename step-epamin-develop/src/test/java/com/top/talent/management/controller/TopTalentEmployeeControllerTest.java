package com.top.talent.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.top.talent.management.constants.RatingStatus;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.MasterDataResponseDTO;
import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.exception.InvalidFileFormatException;
import com.top.talent.management.security.CustomUserPrincipal;

import com.top.talent.management.service.*;
import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.service.CulturalScoreService;
import com.top.talent.management.service.EngXExtraMileRatingService;
import com.top.talent.management.service.TopTalentEmployeeService;
import com.top.talent.management.service.impl.ExcelFileDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TopTalentEmployeeController.class)
class TopTalentEmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopTalentEmployeeService topTalentEmployeeService;

    @MockBean
    private WeightedScoreCalculatorService weightedScoreCalculatorService;

    @MockBean
    private MasterDataService masterDataService;

    @MockBean
    private CulturalScoreService culturalScoreService;
    @MockBean
    private EngXExtraMileRatingService engXExtraMileRatingService;
    @MockBean
    private JwtUtilService jwtUtilService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExcelFileDetailsServiceImpl generateExcelFileDetailsService;

    @Test
     void testUploadSuccess() throws Exception {
        // Mock authentication
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        // Mock MultipartFile (Excel file)
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "STEP_2024_V1.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "sample content".getBytes()
        );

        // Mock data for TopTalentEmployeesDTO
        List<TopTalentEmployeeDTO> employee = List.of(new TopTalentEmployeeDTO(
                "John Doe", 123456L, "Hyderabad", "2021-05-10", "2 years", "Engineer", "Active", "Production",
                "Software Engineering", "John Manager", "Alice PM", "P123", "JF2", "Competency Practice",
                "Java", "Microservices", "Yes", "Profile2020", "Profile2021",
                85D, 4.5, 90L, 88L, 85D,
                87.5, 4L, "Top 5%", "HRBP1", "DH1", true
        ));

        // Mock service behavior
        when(topTalentEmployeeService.parseAndSaveExcel(any(MultipartFile.class), any(CustomUserPrincipal.class)))
                .thenReturn(employee);

        // Perform the mock request
        mockMvc.perform(multipart("/step/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].Name").value("John Doe"))
                .andExpect(jsonPath("$[0].UID").value(123456L))
                .andExpect(jsonPath("$[0].Location").value("Hyderabad"))
                .andExpect(jsonPath("$[0].DOJ").value("2021-05-10"));
    }

    @Test
    void testUploadFailure_WithInvalidFileName() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "text/plain",
                "invalid content".getBytes()
        );

        when(topTalentEmployeeService.parseAndSaveExcel(any(MultipartFile.class), any(CustomUserPrincipal.class)))
                .thenThrow(new InvalidFileFormatException("Invalid file format"));

        mockMvc.perform(multipart("/step/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadFailure_WithInvalidFileFormat() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "STEP_2024_V3.txt",
                "text/plain",
                "invalid content".getBytes()
        );

        when(topTalentEmployeeService.parseAndSaveExcel(any(MultipartFile.class), any(CustomUserPrincipal.class)))
                .thenThrow(new InvalidFileFormatException("Invalid file format"));

        mockMvc.perform(multipart("/step/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadFailure_WithNoAccess() throws Exception {
        // Simulate an unauthorized user with the ROLE_USER role
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);

        // Create a sample MultipartFile (Excel file)
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "STEP_2024_V1.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "sample content".getBytes()
        );

        mockMvc.perform(multipart("/step/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals("{\"errorMessage\":\"You do not have permission to access this resource.\",\"errors\":null}", result.getResponse().getContentAsString()));
    }


    @Test
    void testGetAllEmployeesSuccess_WithRoleSA() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        List<TopTalentEmployeeDTO> employee = List.of(new TopTalentEmployeeDTO(
                "John Doe", 123456L, "Hyderabad", "2021-05-10", "2 years", "Engineer", "Active", "Production",
                "Software Engineering", "John Manager", "Alice PM", "P123", "JF2", "Competency Practice",
                "Java", "Microservices", "Yes", "Profile2020", "Profile2021",
                85D, 4.5, 90L, 88L, 85D,
                87.5, 4L, "Top 5%", "HRBP1", "DH1", true
        ));

        when(topTalentEmployeeService.getAllEmployeeDataByLatestVersion()).thenReturn(employee);

        mockMvc.perform(get("/step/employees").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].UID").value(123456L))
                .andExpect(jsonPath("$[0].Location").value("Hyderabad"));
    }

    @Test
    void testGetAllEmployeesSuccess_WithRoleP() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);

        List<TopTalentEmployeeDTO> employee = List.of(new TopTalentEmployeeDTO(
                "John Doe", 123456L, "Hyderabad", "2021-05-10", "2 years", "Engineer", "Active", "Production",
                "Software Engineering", "John Manager", "Alice PM", "P123", "JF2", "Competency Practice",
                "Java", "Microservices", "Yes", "Profile2020", "Profile2021",
                85D, 4.5, 90L, 88L, 85D,
                87.5, 4L, "Top 5%", "HRBP1", "DH1", true
        ));

        when(topTalentEmployeeService.getAllEmployeeDataByLatestVersion()).thenReturn(employee);

        mockMvc.perform(get("/step/employees").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].UID").value(123456L))
                .andExpect(jsonPath("$[0].Location").value("Hyderabad"));
    }

    @Test
    void testGetAllEmployeesSuccess_WithRoleSU() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_USER);

        List<TopTalentEmployeeDTO> employee = List.of(new TopTalentEmployeeDTO(
                "John Doe", 123456L, "Hyderabad", "2021-05-10", "2 years", "Engineer", "Active", "Production",
                "Software Engineering", "John Manager", "Alice PM", "P123", "JF2", "Competency Practice",
                "Java", "Microservices", "Yes", "Profile2020", "Profile2021",
                85D, 4.5, 90L, 88L, 85D,
                87.5, 4L, "Top 5%", "HRBP1", "DH1", true
        ));

        when(topTalentEmployeeService.getAllEmployeeDataByLatestVersion()).thenReturn(employee);

        mockMvc.perform(get("/step/employees").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].UID").value(123456L))
                .andExpect(jsonPath("$[0].Location").value("Hyderabad"));
    }

    @Test
    void testGetAllEmployeesFailure_WithRoleU() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);

        mockMvc.perform(get("/step/employees").principal(authentication))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetEmployeeDataByUid_WithValidRole() throws Exception {
        Long uid = 1L;  // Example UID
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_USER);

        mockMvc.perform(get("/step/employees/{uid}", uid)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetEmployeeDataByUid_WithNoAccess() throws Exception {
        Long uid = 1L;  // Example UID
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);

        mockMvc.perform(get("/step/employees/{uid}", uid)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals("{\"errorMessage\":\"You do not have permission to access this resource.\",\"errors\":null}", result.getResponse().getContentAsString()));
    }

    @Test
    void testGetTalentProfileData_WithValidRole() throws Exception {
        Long uid = 1L;  // Example UID
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);

        mockMvc.perform(get("/step/talent-profile/{uid}", uid)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetTalentProfileData_WithNoAccess() throws Exception {
        Long uid = 1L;  // Example UID
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);

        mockMvc.perform(get("/step/talent-profile/{uid}", uid)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals("{\"errorMessage\":\"You do not have permission to access this resource.\",\"errors\":null}", result.getResponse().getContentAsString()));
    }


    @Test
    @WithMockUser(username = "user", roles = "SA")
    void testViewMasterData() throws Exception {
        // Mock the service's response
        MasterDataResponseDTO mockResponse = new MasterDataResponseDTO();
        mockResponse.setRatingStatus(RatingStatus.COMPLETED); // Set a valid value
        mockResponse.setSubmissionStatus(SubmissionStatus.S); // Example additional field
        when(masterDataService.viewMasterData(null)).thenReturn(mockResponse);

        // Perform the GET request and validate the response
        mockMvc.perform(get("/step/master-data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ratingStatus").value("COMPLETED")) // Assert `ratingStatus`
                .andExpect(jsonPath("$.submissionStatus").value("S")); // Example assertion
    }

    @Test
    void testViewMasterDataWithInvalidFileName() throws Exception {
        String fileName = "invalidFileName";

        // Mock service to throw exception when an invalid file name is passed
        when(masterDataService.viewMasterData(fileName)).thenThrow(new IllegalArgumentException("Invalid file name"));

        // Perform the test
        mockMvc.perform(get("/master-data")
                        .param("fileName", fileName)  // Pass the invalid file name
                        .with(csrf())  // CSRF token for security
                        .with(user("admin").roles("SUPER_ADMIN")))  // Assuming admin has SUPER_ADMIN role
                .andExpect(status().isNotFound());  // Expecting a 404 Not Found response
    }


    @Test
     void testViewMasterDataUnauthorizedAccess() throws Exception {
        // Perform the test without any authentication (no user provided)
        mockMvc.perform(get("/master-data")
                        .with(csrf()))  // CSRF token for security
                .andExpect(status().isUnauthorized());  // Expecting a 401 Unauthorized response
    }


    @Test
    void testSaveMasterData() throws Exception {
        MasterDataResponseDTO mockResponse = new MasterDataResponseDTO();
        mockResponse.setRatingStatus(RatingStatus.COMPLETED); // Set a valid value
        mockResponse.setSubmissionStatus(SubmissionStatus.S); // Example additional field

       Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        when(masterDataService.saveEmployeesOfExcelVersion(SubmissionStatus.S,List.of(1L,2L,3L), (CustomUserPrincipal) authentication.getPrincipal())).thenReturn(mockResponse);


        mockMvc.perform(MockMvcRequestBuilders.post("/step/save-master-data")
                        .principal(authentication)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("submissionStatus", "D")  // Adjust the status accordingly
                        .content("[1,2,3]")   // Example data
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Expect 200 OK status
    }

    @Test
    void testGetUserProfile() throws Exception {

        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        when(masterDataService.getUserProfile()).thenReturn(Map.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/step/user-profile")
                        .principal(authentication)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}