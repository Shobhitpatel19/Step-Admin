package com.top.talent.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.FutureSkillPracticeDTO;
import com.top.talent.management.dto.FutureSkillRequestListDTO;
import com.top.talent.management.dto.FutureSkillResponseDTO;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.FutureSkillService;

import com.top.talent.management.service.impl.JwtUtilService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(FutureSkillController.class)
class FutureSkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FutureSkillService futureSkillService;

    @MockBean
    private JwtUtilService jwtUtilService;

    @Autowired
    private ObjectMapper objectMapper;

    private Authentication authentication;

    private void setupSecurityContext(Authentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetFutureSkills_Successful() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);
        setupSecurityContext(authentication);

        FutureSkillResponseDTO responseDTO = FutureSkillResponseDTO.builder()
                .practiceHeadName("John Doe")
                .practiceName("IT")
                .build();

        when(futureSkillService.getFutureSkill(any(CustomUserPrincipal.class))).thenReturn(responseDTO);

        mockMvc.perform(get("/step/future-skills")
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.practiceHeadName").value("John Doe"));

        verify(futureSkillService, times(1)).getFutureSkill(any(CustomUserPrincipal.class));
    }


    @Test
    void testSaveFutureSkills_Successful() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);
        setupSecurityContext(authentication);

        FutureSkillRequestListDTO requestListDTO = FutureSkillRequestListDTO.builder().build();
        String responseMessage = Constants.FUTURE_SKILL_STATUS_S;

        when(futureSkillService.saveFutureSkill(any(CustomUserPrincipal.class), any(FutureSkillRequestListDTO.class)))
                .thenReturn(responseMessage);

        mockMvc.perform(post("/step/future-skills")
                        .principal(authentication)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestListDTO)))
                .andExpect(status().isOk());

        verify(futureSkillService, times(1)).saveFutureSkill(any(CustomUserPrincipal.class), any(FutureSkillRequestListDTO.class));
    }

    @Test
    void testSaveFutureSkills_Unauthorized() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        setupSecurityContext(authentication);

        FutureSkillRequestListDTO requestListDTO = FutureSkillRequestListDTO.builder().build();

        mockMvc.perform(post("/step/future-skills")
                        .principal(authentication)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestListDTO)))
                .andExpect(status().isForbidden());

        verify(futureSkillService, never()).saveFutureSkill(any(CustomUserPrincipal.class), any(FutureSkillRequestListDTO.class));
    }

    @Test
    void testGetPracticeDetailsAndSubmissionStatuses_Successful() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
        setupSecurityContext(authentication);

        List<FutureSkillPracticeDTO> futureSkillPracticeList = List.of(
                FutureSkillPracticeDTO.builder()
                        .practiceName("IT")
                        .practiceHeadName("John Doe")
                        .submissionStatus("Active")
                        .date("2023-10-01")
                        .skills("Java, Spring Boot")
                        .submittedBy("Jane Doe")
                        .build()
        );

        when(futureSkillService.getPracticeDetailsAndSubmissionStatus()).thenReturn(futureSkillPracticeList);

        mockMvc.perform(get("/step/practices/future-skills")
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].practiceName").value("IT"))
                .andExpect(jsonPath("$[0].practiceHeadName").value("John Doe"))
                .andExpect(jsonPath("$[0].submissionStatus").value("Active"))
                .andExpect(jsonPath("$[0].date").value("2023-10-01"))
                .andExpect(jsonPath("$[0].skills").value("Java, Spring Boot"))
                .andExpect(jsonPath("$[0].submittedBy").value("Jane Doe"));

        verify(futureSkillService, times(1)).getPracticeDetailsAndSubmissionStatus();
    }

    @Test
    void testGetPracticeDetailsAndSubmissionStatuses_AccessDenied() throws Exception {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        setupSecurityContext(authentication);

        mockMvc.perform(get("/step/practices/future-skills")
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(futureSkillService, never()).getPracticeDetailsAndSubmissionStatus();
    }


}
