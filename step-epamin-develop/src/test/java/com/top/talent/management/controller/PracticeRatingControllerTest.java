package com.top.talent.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;

import com.top.talent.management.dto.CategoryDTO;
import com.top.talent.management.dto.CategoryRequestDTO;
import com.top.talent.management.dto.EmployeeRatingRequestDTO;
import com.top.talent.management.dto.EmployeeRatingResponseDTO;
import com.top.talent.management.dto.PracticeEmployeeDTO;
import com.top.talent.management.dto.PracticeRatingResponseDTO;
import com.top.talent.management.dto.SubCategoryDTO;
import com.top.talent.management.dto.SubCategoryRatingDTO;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.service.PracticeRatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PracticeRatingController.class)
class PracticeRatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PracticeRatingService practiceRatingService;

    @MockBean
    private JwtUtilService jwtUtilService;

    @Autowired
    private ObjectMapper objectMapper;

    private final long uid = 653000L;

    private final String baseUrl = "/step/practice-rating";

    private Authentication authentication;

    @Test
    void getCompetencies_AuthorizedUser_ReturnsCompetencies() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
        List<String> expectedCompetencies = List.of("Java", "Python", "SQL");

        when(practiceRatingService.getCompetencies()).thenReturn(expectedCompetencies);

        mockMvc.perform(get(baseUrl+"/competencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"Java\",\"Python\",\"SQL\"]"));
    }

    @Test
    void getCompetencies_UnauthorizedUser_ReturnsForbidden() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);

        mockMvc.perform(get(baseUrl+"/competencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isForbidden());

        verifyNoInteractions(practiceRatingService);
    }

    @Test
    void testGetEmployees_Success_Role_Practice() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);

        when(practiceRatingService.getCandidates(anyString(), eq(Constants.DEFAULT_COMPETENCY))).thenReturn(getMockPracticeRatingResponseDTO("Java"));

        mockMvc.perform(get(baseUrl+"/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.competency").value("Java"))
                .andExpect(jsonPath("$.users[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$.users[1].fullName").value("Jane Smith"));

        verify(practiceRatingService, times(1)).getCandidates(anyString(), eq(Constants.DEFAULT_COMPETENCY));
    }

    @Test
    void testGetEmployees_Success_Role_SuperAdmin() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        when(practiceRatingService.getCandidates(anyString(), eq(Constants.DEFAULT_COMPETENCY))).thenReturn(getMockPracticeRatingResponseDTO("All"));

        mockMvc.perform(get(baseUrl+"/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.competency").value("All"))
                .andExpect(jsonPath("$.users[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$.users[1].fullName").value("Jane Smith"));

        verify(practiceRatingService, times(1)).getCandidates(anyString(), eq(Constants.DEFAULT_COMPETENCY));
    }

    @Test
    void testGetEmployees_Success_WithCompetency_Role_SuperAdmin() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
        String competency = "Java";

        when(practiceRatingService.getCandidates(anyString(), eq(competency))).thenReturn(getMockPracticeRatingResponseDTO(competency));

        mockMvc.perform(get(baseUrl+"/employees?competency=" + competency)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.competency").value(competency))
                .andExpect(jsonPath("$.users[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$.users[1].fullName").value("Jane Smith"));

        verify(practiceRatingService, times(1)).getCandidates(anyString(), eq(competency));
    }

    @Test
    void testGetEmployees_Forbidden() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);

        // Perform GET request with incorrect role
        mockMvc.perform(get(baseUrl+"/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication))
                .andExpect(status().isForbidden());

        verifyNoInteractions(practiceRatingService);
    }

    @Test
    void testGetEmployeeRating_Status_NA_Role_Practice() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);

        when(practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal())).thenReturn(getMockEmployeeRatingResponseDTO(null));

        mockMvc.perform(
                        get(baseUrl+"/employees/get-rating/{uid}", uid)
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").doesNotExist());


        verify(practiceRatingService, times(1)).getEmployeeRating(eq(uid), any(CustomUserPrincipal.class));
    }

    @Test
    void testGetEmployeeRating_Status_D_Role_Practice() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);

        when(practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal())).thenReturn(getMockEmployeeRatingResponseDTO(Constants.DRAFT_STATUS));

        mockMvc.perform(
                        get(baseUrl+"/employees/get-rating/{uid}", uid)
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").value(Constants.DRAFT_STATUS));


        verify(practiceRatingService, times(1)).getEmployeeRating(eq(uid), any(CustomUserPrincipal.class));
    }

    @Test
    void testGetEmployeeRating_Status_S_Role_Practice() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);

        when(practiceRatingService.getEmployeeRating(anyLong(), any(CustomUserPrincipal.class))).thenReturn(getMockEmployeeRatingResponseDTO(Constants.SUBMIT_STATUS));

        mockMvc.perform(
                        get(baseUrl+"/employees/get-rating/{uid}", uid)
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").value(Constants.SUBMIT_STATUS));


        verify(practiceRatingService, times(1)).getEmployeeRating(eq(uid), any(CustomUserPrincipal.class));
    }

    @Test
    void testGetEmployeeRating_Status_NA_Role_SuperAdmin() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        when(practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal())).thenReturn(getMockEmployeeRatingResponseDTO(null));

        mockMvc.perform(
                        get(baseUrl+"/employees/get-rating/{uid}", uid)
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").doesNotExist());


        verify(practiceRatingService, times(1)).getEmployeeRating(eq(uid), any(CustomUserPrincipal.class));
    }

    @Test
    void testGetEmployeeRating_Status_D_Role_SuperAdmin() throws Exception {

        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        when(practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal())).thenReturn(getMockEmployeeRatingResponseDTO(Constants.DRAFT_STATUS));

        mockMvc.perform(
                        get(baseUrl+"/employees/get-rating/{uid}", uid)
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").value(Constants.DRAFT_STATUS));


        verify(practiceRatingService, times(1)).getEmployeeRating(eq(uid), any(CustomUserPrincipal.class));
    }

    @Test
    void testGetEmployeeRating_Status_S_Role_SuperAdmin() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        when(practiceRatingService.getEmployeeRating(anyLong(), any(CustomUserPrincipal.class))).thenReturn(getMockEmployeeRatingResponseDTO(Constants.SUBMIT_STATUS));

        mockMvc.perform(
                        get(baseUrl+"/employees/get-rating/{uid}", uid)
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").value(Constants.SUBMIT_STATUS));


        verify(practiceRatingService, times(1)).getEmployeeRating(eq(uid), any(CustomUserPrincipal.class));
    }

    @Test
    void testUpdateEmployeeRatingsSuccess_Status_D_Role_Practice() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);

        SubmissionStatus status = SubmissionStatus.D;
        EmployeeRatingRequestDTO requestDTO = getMockEmployeeRatingRequestDTO();

        when(practiceRatingService.saveEmployeeRatings(
                        eq(uid),
                        eq(status),
                        any(EmployeeRatingRequestDTO.class),
                        any(CustomUserPrincipal.class)
                )
        ).thenReturn(getMockEmployeeRatingResponseDTO(Constants.DRAFT_STATUS));

        mockMvc.perform(post(baseUrl+"/employees/save-rating/{uid}", uid)
                        .param("submissionStatus", status.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .principal(authentication)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").value(Constants.DRAFT_STATUS));

        verify(practiceRatingService, times(1)).saveEmployeeRatings(eq(uid), eq(status), any(EmployeeRatingRequestDTO.class), any(CustomUserPrincipal.class));
    }

    @Test
    void testUpdateEmployeeRatingsSuccess_Status_S_Role_Practice() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);

        SubmissionStatus status = SubmissionStatus.S;
        EmployeeRatingRequestDTO requestDTO = getMockEmployeeRatingRequestDTO();

        when(practiceRatingService.saveEmployeeRatings(
                        eq(uid),
                        eq(status),
                        any(EmployeeRatingRequestDTO.class),
                        any(CustomUserPrincipal.class)
                )
        ).thenReturn(getMockEmployeeRatingResponseDTO(Constants.SUBMIT_STATUS));

        mockMvc.perform(post(baseUrl+"/employees/save-rating/{uid}", uid)
                        .param("submissionStatus", status.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .principal(authentication)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").value(Constants.SUBMIT_STATUS));

        verify(practiceRatingService, times(1)).saveEmployeeRatings(eq(uid), eq(status), any(EmployeeRatingRequestDTO.class), any(CustomUserPrincipal.class));
    }


    @Test
    void testUpdateEmployeeRatingsSuccess_Status_D_Role_SuperAdmin() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        SubmissionStatus status = SubmissionStatus.D;
        EmployeeRatingRequestDTO requestDTO = getMockEmployeeRatingRequestDTO();

        when(practiceRatingService.saveEmployeeRatings(
                        eq(uid),
                        eq(status),
                        any(EmployeeRatingRequestDTO.class),
                        any(CustomUserPrincipal.class)
                )
        ).thenReturn(getMockEmployeeRatingResponseDTO(Constants.DRAFT_STATUS));

        mockMvc.perform(post(baseUrl+"/employees/save-rating/{uid}", uid)
                        .param("submissionStatus", status.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .principal(authentication)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").value(Constants.DRAFT_STATUS));

        verify(practiceRatingService, times(1)).saveEmployeeRatings(eq(uid), eq(status), any(EmployeeRatingRequestDTO.class), any(CustomUserPrincipal.class));
    }

    @Test
    void testUpdateEmployeeRatingsSuccess_Status_S_Role_SuperAdmin() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        SubmissionStatus status = SubmissionStatus.S;
        EmployeeRatingRequestDTO requestDTO = getMockEmployeeRatingRequestDTO();

        when(practiceRatingService.saveEmployeeRatings(
                        eq(uid),
                        eq(status),
                        any(EmployeeRatingRequestDTO.class),
                        any(CustomUserPrincipal.class)
                )
        ).thenReturn(getMockEmployeeRatingResponseDTO(Constants.SUBMIT_STATUS));

        mockMvc.perform(post(baseUrl+"/employees/save-rating/{uid}", uid)
                        .param("submissionStatus", status.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .principal(authentication)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mean").value(4.5))
                .andExpect(jsonPath("$.categories[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.categories[0].subCategory[0].subCategoryName").value("subcategory1"))
                .andExpect(jsonPath("$.message").value(Constants.SUBMIT_STATUS));

        verify(practiceRatingService, times(1)).saveEmployeeRatings(eq(uid), eq(status), any(EmployeeRatingRequestDTO.class), any(CustomUserPrincipal.class));
    }

    @Test
    void testApproveAllEmployeesRatings() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);


        when(practiceRatingService.approveAllEmployeesRatings()).thenReturn(true);

        mockMvc.perform(post(baseUrl+"/employees/approve-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

        verify(practiceRatingService, times(1)).approveAllEmployeesRatings();
    }

    private CustomUserPrincipal getCustomUserPrincipal()
    {
        return (CustomUserPrincipal) authentication.getPrincipal();
    }

    private PracticeRatingResponseDTO getMockPracticeRatingResponseDTO(String competency)
    {
        PracticeEmployeeDTO candidate1 = PracticeEmployeeDTO.builder()
                .fullName("John Doe")
                .uid(1L)
                .jobDesignation("Developer")
                .primarySkill("Java")
                .photo("https://image1.url")
                .build();

        PracticeEmployeeDTO candidate2 = PracticeEmployeeDTO.builder()
                .fullName("Jane Smith")
                .uid(2L)
                .jobDesignation("Senior Developer")
                .primarySkill("Spring Boot")
                .photo("https://image2.url")
                .build();

        return PracticeRatingResponseDTO.builder()
                .competency(competency)
                .users(List.of(candidate1, candidate2))
                .build();
    }

    private EmployeeRatingRequestDTO getMockEmployeeRatingRequestDTO()
    {
        return EmployeeRatingRequestDTO.builder()
                .categories(List.of(getCategoryRequestDTO()))
                .build();
    }

    private CategoryRequestDTO getCategoryRequestDTO()
    {
        SubCategoryRatingDTO subCategoryRatingDTO = SubCategoryRatingDTO.builder()
                .subCategoryName("subcategory1")
                .employeeRating(4.0)
                .build();

        return CategoryRequestDTO.builder()
                .categoryName("category1")
                .subCategory(List.of(subCategoryRatingDTO))
                .build();
    }

    private EmployeeRatingResponseDTO getMockEmployeeRatingResponseDTO(String msg)
    {
        SubCategoryDTO subCategoryDTO = SubCategoryDTO.builder()
                .subCategoryName("subcategory1")
                .employeeRating(4.0)
                .build();

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryName("category1")
                .subCategory(List.of(subCategoryDTO))
                .build();

        return EmployeeRatingResponseDTO.builder()
                .mean(4.5)
                .categories(List.of(categoryDTO))
                .message(msg)
                .build();
    }
}