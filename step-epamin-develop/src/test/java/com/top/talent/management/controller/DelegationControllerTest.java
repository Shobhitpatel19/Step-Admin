package com.top.talent.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.Delegation;
import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.service.PracticeDelegationFeatureService;
import com.top.talent.management.service.DelegationService;
import com.top.talent.management.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DelegationController.class)
class DelegationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtilService jwtUtilService;

    @MockBean
    private DelegationService delegationService;

    @MockBean
    private PracticeDelegationFeatureService practiceDelegationFeatureService;


    private Authentication authentication;

    @BeforeEach
    public void setup() {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);
    }

    @Test
     void testCreatePracticeDelegate() throws Exception {
        Delegation delegation = Delegation.builder().build();
        String competency = "Java";

        mockMvc.perform(
                        post("/step/delegate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(delegation))
                                .param("competency", competency) // Passing competency as request param
                                .principal(authentication)
                                .with(csrf())
                )
                .andExpect(status().isOk());

        verify(delegationService, times(1))
                .createPracticeDelegate(delegation, competency);
    }

    @Test
     void testGetPracticeDelegate() throws Exception {
        String competency = "Java";

        mockMvc.perform(
                        get("/step/delegate")
                                .param("competency", competency) // Passing competency
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(delegationService, times(1))
                .getPracticeDelegate(competency);
    }

    @Test
    void testDeletePracticeDelegate() throws Exception {
        String competency = "Java";

        mockMvc.perform(
                        delete("/step/delegate")
                                .param("competency", competency) // Passing competency
                                .principal(authentication)
                                .with(csrf())
                )
                .andExpect(status().isOk());

        verify(delegationService, times(1))
                .deletePracticeDelegate(competency);
    }

    @Test
    void testGetAvailablePracticeFeaturesToDelegate() throws Exception {
        mockMvc.perform(
                        get("/step/get-available-practice-features")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(practiceDelegationFeatureService, times(1))
                .getAllFeaturesName();
    }

    @Test
    void testWithInvalidRole() throws Exception {
        Authentication userAuthentication = TestUtils.getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        String competency = "Java";

        mockMvc.perform(
                        get("/step/delegate")
                                .param("competency", competency)
                                .principal(userAuthentication)
                )
                .andExpect(status().isForbidden());

        verify(delegationService, times(0))
                .getPracticeDelegate(competency);
    }
}