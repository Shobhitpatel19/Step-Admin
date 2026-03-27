package com.top.talent.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserResponseDTO;
import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.service.impl.SuperAdminServiceImpl;
import com.top.talent.management.service.UserService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SuperAdminController.class)
class SuperAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtilService jwtUtilService;

    @MockBean
    private SuperAdminServiceImpl superAdminService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String url = "/step/super-admin";

    @Test
    void testGrantAccess_Success() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
        List<UserDTO> mockUsers = List.of(UserDTO.builder().build());

        when(superAdminService.grantAccessToUserRole()).thenReturn(mockUsers);

        mockMvc.perform(post(url + "/grant-access")
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUsersByRoleSuperAdminSuccess() throws Exception {

        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        when(userService.getUsersByRole(anyString(), any())).thenReturn(
                List.of(getMockUserResponseDTO(Constants.USER_STATUS_ACTIVE), getMockUserResponseDTO(Constants.USER_STATUS_INACTIVE)));

        mockMvc.perform(get(url+"/access-privileges?roleName="+RoleConstants.ROLE_SUPER_USER)
                        .principal(authentication)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].firstName").value("Test"))
                .andExpect(jsonPath("$[0].lastName").value("User"))
                .andExpect(jsonPath("$[0].practice").value("TestPractice"))
                .andExpect(jsonPath("$[0].roleName").value(RoleConstants.ROLE_SUPER_USER))
                .andExpect(jsonPath("$[0].status").value(Constants.USER_STATUS_ACTIVE))
                .andExpect(jsonPath("$[1].status").value(Constants.USER_STATUS_INACTIVE))
                .andDo(print());

        verify(userService, times(1)).getUsersByRole(anyString(), any());
    }

    @Test
    void testGetUsersByRoleSuperAdminSuccess_StatusActive() throws Exception {

        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        when(userService.getUsersByRole(anyString(), any())).thenReturn(
                List.of(getMockUserResponseDTO(Constants.USER_STATUS_ACTIVE)));

        mockMvc.perform(get(url+"/access-privileges?roleName="+RoleConstants.ROLE_SUPER_USER)
                        .principal(authentication)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].firstName").value("Test"))
                .andExpect(jsonPath("$[0].lastName").value("User"))
                .andExpect(jsonPath("$[0].practice").value("TestPractice"))
                .andExpect(jsonPath("$[0].roleName").value(RoleConstants.ROLE_SUPER_USER))
                .andExpect(jsonPath("$[0].status").value(Constants.USER_STATUS_ACTIVE))
                .andDo(print());

        verify(userService, times(1)).getUsersByRole(anyString(), any());
    }

    @Test
    void testGetUsersByRoleSuperAdminSuccess_StatusInactive() throws Exception {

        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        when(userService.getUsersByRole(anyString(), any())).thenReturn(
                List.of(getMockUserResponseDTO(Constants.USER_STATUS_INACTIVE)));

        mockMvc.perform(get(url+"/access-privileges?roleName="+RoleConstants.ROLE_SUPER_USER)
                        .principal(authentication)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].firstName").value("Test"))
                .andExpect(jsonPath("$[0].lastName").value("User"))
                .andExpect(jsonPath("$[0].practice").value("TestPractice"))
                .andExpect(jsonPath("$[0].roleName").value(RoleConstants.ROLE_SUPER_USER))
                .andExpect(jsonPath("$[0].status").value(Constants.USER_STATUS_INACTIVE))
                .andDo(print());

        verify(userService, times(1)).getUsersByRole(anyString(), any());
    }

    @Test
    void testAddUser() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
        UserDTO requestDTO = getMockUserDTO();
        UserResponseDTO responseDTO = getMockUserResponseDTO(Constants.USER_STATUS_ACTIVE);

        when(userService.addUser(any(UserDTO.class), anyString())).thenReturn(responseDTO);

        mockMvc.perform(post(url+"/access-privileges/add-user")
                        .principal(authentication)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.practice").value("TestPractice"))
                .andExpect(jsonPath("$.roleName").value(RoleConstants.ROLE_SUPER_USER))
                .andExpect(jsonPath("$.status").value(Constants.USER_STATUS_ACTIVE))
                .andDo(print());

        verify(userService, times(1)).addUser(any(UserDTO.class), anyString());
    }

    @Test
    void testDeactivateUser() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
        UserResponseDTO responseDTO = getMockUserResponseDTO(Constants.USER_STATUS_ACTIVE);

        when(userService.deactivateUser(eq(1L), anyString())).thenReturn(responseDTO);

        mockMvc.perform(delete(url+"/access-privileges/deactivate-user/1")
                        .principal(authentication)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.practice").value("TestPractice"))
                .andExpect(jsonPath("$.roleName").value(RoleConstants.ROLE_SUPER_USER))
                .andExpect(jsonPath("$.status").value(Constants.USER_STATUS_ACTIVE))
                .andDo(print());

        verify(userService, times(1)).deactivateUser(eq(1L), anyString());
    }

    private UserResponseDTO getMockUserResponseDTO(String status)
    {
        return UserResponseDTO.builder()
                .uuid(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .practice("TestPractice")
                .roleName(RoleConstants.ROLE_SUPER_USER)
                .status(status)
                .build();
    }

    private UserDTO getMockUserDTO()
    {
        return UserDTO.builder()
                .uuid(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .practice("TestPractice")
                .roleName(RoleConstants.ROLE_SUPER_USER)
                .build();
    }
}
