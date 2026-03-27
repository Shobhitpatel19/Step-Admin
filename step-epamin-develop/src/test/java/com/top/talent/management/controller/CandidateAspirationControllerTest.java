package com.top.talent.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.top.talent.management.constants.AspirationPriority;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.AspirationApprovalRequestDTO;
import com.top.talent.management.dto.AspirationDTO;
import com.top.talent.management.dto.AspirationPriorityDTO;
import com.top.talent.management.dto.AspirationResponseDTO;
import com.top.talent.management.dto.SubmitAspirationsRequest;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.CandidateAspirationService;
import com.top.talent.management.service.impl.JwtUtilService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = CandidateAspirationController.class)
class CandidateAspirationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CandidateAspirationService aspirationService;

    @MockBean
    private JwtUtilService jwtUtilService;

    @Test
    void testGetAspirations_Forbidden() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
        mockMvc.perform(get("/step/aspirations")
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAspirations_Success() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        AspirationResponseDTO responseDTO = AspirationResponseDTO.builder().build();
        when(aspirationService.getAspirations((CustomUserPrincipal) authentication.getPrincipal())).thenReturn(responseDTO);

        mockMvc.perform(get("/step/aspirations")
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDTO)));
    }

    @Test
    void testCreateAspiration_Success() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        AspirationDTO aspirationDTO = AspirationDTO.builder().build();
        String requestPayload = objectMapper.writeValueAsString(aspirationDTO);
        when(aspirationService.saveAspiration((CustomUserPrincipal) authentication.getPrincipal(), aspirationDTO))
                .thenReturn(aspirationDTO);

        mockMvc.perform(post("/step/aspirations")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(aspirationDTO)));
    }

    @Test
    void testDeleteAspiration_Success() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        String priority = "ASPIRATION1";
        when(aspirationService.deleteAspiration((CustomUserPrincipal) authentication.getPrincipal(),
                AspirationPriority.valueOf(priority))).thenReturn(AspirationDTO.builder().build());

        mockMvc.perform(delete("/step/aspirations/"+ priority)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAspirationByPriority_Success() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        String priority = "ASPIRATION1";
        AspirationPriorityDTO aspirationPriorityDTO = AspirationPriorityDTO.builder().build();
        when(aspirationService.getAspirationByPriority((CustomUserPrincipal) authentication.getPrincipal(), AspirationPriority.valueOf(priority))).thenReturn(aspirationPriorityDTO);

        mockMvc.perform(get("/step/aspirations/"+ priority)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(aspirationPriorityDTO)));
    }

    @Test
    void testEditAspiration_Success() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        String priority = "ASPIRATION1";
        AspirationDTO aspirationDTO =AspirationDTO.builder().build();
        String requestPayload = objectMapper.writeValueAsString(aspirationDTO);
        when(aspirationService.editAspiration((CustomUserPrincipal) authentication.getPrincipal(),
                AspirationPriority.valueOf(priority), aspirationDTO)).thenReturn(aspirationDTO);

        mockMvc.perform(put("/step/aspirations/"+ priority)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(aspirationDTO)));
    }

    @Test
    void testSubmitAspirations_Success() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        SubmitAspirationsRequest submitRequest = new SubmitAspirationsRequest(true);

        when(aspirationService.submitAspirations((CustomUserPrincipal) authentication.getPrincipal(), submitRequest)).thenReturn(List.of());

        mockMvc.perform(post("/step/aspirations/submit")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(submitRequest))
                        .with(csrf()))
                .andExpect(status().isOk());

    }

    @Test
    void testGetSubmittedAspirationsForApproval_Success() throws Exception {
        AspirationResponseDTO responseDTO = AspirationResponseDTO.builder()
                .aspirationExplanation(List.of("Aspiration 1 explanation", "Aspiration 2 explanation"))
                .aspirations(List.of())
                .previousYearAspirations(List.of())
                .isFormActive(true)
                .build();

        when(aspirationService.getSubmittedAspirationsForApproval()).thenReturn(List.of(responseDTO));

        CustomUserPrincipal principal = new CustomUserPrincipal(
                "PracticeUser",
                "Test",
                "practice_user@domain.com",
                RoleConstants.PRACTICE,
                false
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + RoleConstants.PRACTICE))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/step/aspirations/approval")
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(responseDTO))));
    }

    @Test
    void testApproveAspiration_Success() throws Exception {
        AspirationApprovalRequestDTO approvalRequest = new AspirationApprovalRequestDTO();
        approvalRequest.setAssignedRole("Solution Architect");
        approvalRequest.setProficiency("Advanced");
        approvalRequest.setApprovedBy("John Doe");

        String requestPayload = objectMapper.writeValueAsString(approvalRequest);

        doNothing().when(aspirationService).approveAspiration(123L, 1L, approvalRequest);

        CustomUserPrincipal principal = new CustomUserPrincipal(
                "PracticeUser",
                "Test",
                "practice_user@domain.com",
                RoleConstants.PRACTICE,
                false
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + RoleConstants.PRACTICE))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(post("/step/aspirations/123/approve/1")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(aspirationService).approveAspiration(123L, 1L, approvalRequest);
    }

    @Test
    void testGetSubmittedAspirationsForApproval_Forbidden() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);

        mockMvc.perform(get("/step/aspirations/approval")
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testApproveAspiration_Forbidden() throws Exception {

        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);

        AspirationApprovalRequestDTO approvalRequest = new AspirationApprovalRequestDTO();
        approvalRequest.setAssignedRole("Solution Architect");
        approvalRequest.setProficiency("Advanced");
        approvalRequest.setApprovedBy("John Doe");

        String requestPayload = objectMapper.writeValueAsString(approvalRequest);

        mockMvc.perform(post("/step/aspirations/123/approve/1")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestPayload)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

}

