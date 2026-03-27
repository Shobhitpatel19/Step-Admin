package com.top.talent.management.controller;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.service.PracticeDelegateUserService;
import com.top.talent.management.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PracticeDelegateUserController.class)
class PracticeDelegateUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtilService jwtUtilService;

    @MockBean
    private PracticeDelegateUserService practiceDelegateUserService;

    private Authentication authentication;

    @BeforeEach
    public void setup(){
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);
    }

    @Test
    void testGetDelegatedFeatures() throws Exception{
        mockMvc.perform(
                        get("/step/get-delegated-features")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(practiceDelegateUserService, times(1)).getDelegatedFeatures();
    }

    @Test
    void testGetIsApprovalRequired() throws Exception{
        mockMvc.perform(
                        get("/step/is-approval-required")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(practiceDelegateUserService, times(1)).isApprovalRequired();
    }

    @Test
    void testGetHasAccessToFeature() throws Exception{
        String featureName = "Feature1";
        mockMvc.perform(
                        get("/step/has-access-to-feature")
                                .param("featureName", featureName)
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(practiceDelegateUserService, times(1))
                .hasAccessToFeature(featureName);
    }

    @Test
    void testWithInvalidRole() throws Exception{
        Authentication userAuthentication = TestUtils.getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);

        mockMvc.perform(
                        get("/step/get-delegated-features")
                                .principal(userAuthentication)
                )
                .andExpect(status().isForbidden());

        verify(practiceDelegateUserService, times(0))
                .getDelegatedFeatures();
    }

}
