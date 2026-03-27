package com.top.talent.management.controller;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtilService jwtUtilService;

    @MockBean
    private UserProfileService userProfileService;

    private Authentication authentication;

    @BeforeEach
    public void setup() {
        authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
    }

    @Test
    void testSearchEmployee() throws Exception {
        mockMvc.perform(
                        get("/step/full-user-profile")
                                .param("name", "user")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(userProfileService, times(1))
                .fetchUserDetails("user");
    }

    @Test
    void testSearchEmployeeAboveB3() throws Exception {
        mockMvc.perform(
                        get("/step/full-user-profile-above-b3")
                                .param("name", "user")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(userProfileService, times(1))
                .fetchUserDetailsAboveB3("user");
    }

    @Test
    void testSearchPracticeHEads() throws Exception {
        mockMvc.perform(
                        get("/step/all-practice-heads")
                                .param("name", "user")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(userProfileService, times(1))
                .getPracticeHeads();
    }

}
