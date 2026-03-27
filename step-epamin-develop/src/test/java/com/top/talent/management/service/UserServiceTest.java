package com.top.talent.management.service;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserResponseDTO;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.PracticeDelegationException;
import com.top.talent.management.exception.TopTalentEmployeeException;
import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.helper.SuperAdminServiceHelper;
import com.top.talent.management.mapper.UserMapper;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.top.talent.management.utils.TestUtils.getMockAuthentication;
import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static com.top.talent.management.utils.TestUtils.getUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SuperAdminServiceHelper superAdminServiceHelper;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    public UserServiceTest(){
        getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
    }

    @Test
    void testGetUser() {
        User user = createUser();
        when(userRepository.findByEmail("test_user@epam.com")).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(getUserDTO());

        UserDTO result = userService.getUser(getMockAuthentication());
        assertEquals("test_user@epam.com", result.getEmail());
    }


    @Test
    void testGetNullAuthentication() {
        Exception exception = assertThrows(AccessDeniedException.class,
                () -> userService.getUser((Authentication) null));
        assertNotNull(exception);
    }


    @Test
    void testGetAllUsers() {
        User user = createUser();
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userMapper.toUserDTO(user)).thenReturn(getUserDTO());

        List<UserDTO> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("test_user@epam.com", result.get(0).getEmail());
    }


    @Test
    void testGetUsersByPractice() {
        User user = createUser();
        when(userRepository.findByPractice("java")).thenReturn(Collections.singletonList(user));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(getUserDTO());

        List<UserDTO> result = userService.getUsersByPractice("java",
                getMockAuthentication());
        assertEquals(1, result.size());
        assertEquals("test_user@epam.com", result.get(0).getEmail());
    }


    @Test
    void testGetUsersByPracticeNoMatch() {
        String practice = "nonexistent_practice";
        Authentication auth = getMockAuthentication();
        User user = createUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Exception exception = assertThrows(AccessDeniedException.class,
                () -> userService.getUsersByPractice(practice, auth));
        assertNotNull(exception);
    }

    @Test
    void testGetUsersByNullPractice() {
        Authentication auth = getMockAuthentication();

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUsersByPractice(null, auth));
        assertNotNull(exception);
    }

    @Test
    void testGetUsersByEmptyPractice() {
        Authentication auth = getMockAuthentication();
        String emptyPractice = "";

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUsersByPractice(emptyPractice, auth));
        assertNotNull(exception);
    }


    @Test
    void testGetUserByEmail() {
        User user = createUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(getUserDTO());

        UserDTO userDTO = userService.getUser(user.getEmail());
        assertEquals("test_user@epam.com", userDTO.getEmail());
    }

    @Test
    void testRemoveUserByEmail() {
        User user = createUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(getUserDTO());
        doNothing().when(userRepository).delete(user);

        UserDTO userDTO = userService.removeUser(user.getEmail());
        assertEquals("test_user@epam.com", userDTO.getEmail());
    }

    @Test
    void testRegisterUser() {
        User user = createUser();

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(getUserDTO());

        UserDTO userDTO = userService.registerUser(user);
        assertEquals("test_user@epam.com", userDTO.getEmail());
    }

    @Test
    void testAddEligibleUsersWithException()
    {
        List<TopTalentEmployee> emptyList = List.of();

        when(userRepository.saveAll(anyList())).thenThrow(new TopTalentEmployeeException(ErrorMessages.USERS_NOT_SAVED));

        assertThrows(TopTalentEmployeeException.class, ()->userService.addEligibleUsers(emptyList));
    }

    @Test
    void testAddEligibleUsersNoException()
    {
        when(userRepository.saveAll(anyList())).thenReturn(List.of());

        List<User> userDTOS = userService.addEligibleUsers(new ArrayList<>());
        assertEquals(0, userDTOS.size());
    }

    @Test
    void getUsersByRole_WithStatusPresent_ReturnsActiveUsers() {
        Role role = getRole();
        User user1 = getCustomMockUser(1L, "john.doe@example.com","John", "Doe", "Marketing", Constants.USER_STATUS_ACTIVE);
        User user2 = getCustomMockUser(2L, "jane.doe@example.com","Jane", "Doe",  "Engineering", Constants.USER_STATUS_INACTIVE);
        UserResponseDTO dto1 = getCustomMockUserResponseDTO(1L, "john.doe@example.com","John", "Doe",  "Marketing", Constants.USER_STATUS_ACTIVE);
        UserResponseDTO dto2 = getCustomMockUserResponseDTO(2L, "jane.doe@example.com","Jane", "Doe",  "Engineering", Constants.USER_STATUS_INACTIVE);

        when(roleRepository.findByName(RoleConstants.ROLE_USER)).thenReturn(role);
        when(userRepository.findAllByRole(any(Role.class))).thenReturn(List.of(user1, user2));
        when(userMapper.toUserResponseDTO(user1, null)).thenReturn(dto1);

        Optional<String> status = Optional.of(Constants.USER_STATUS_ACTIVE);
        List<UserResponseDTO> result = userService.getUsersByRole(role.getName(), status);

        verify(userRepository).findAllByRole(role);
        verify(userMapper).toUserResponseDTO(user1, null);
        verify(userMapper, never()).toUserResponseDTO(user2, null);
        assertTrue(result.contains(dto1));
        assertFalse(result.contains(dto2));
    }

    @Test
    void getUsersByRole_WithStatusPresent_ReturnsInactiveUsers() {
        Role role = getRole();
        User user1 = getCustomMockUser(1L, "john.doe@example.com","John", "Doe", "Marketing", Constants.USER_STATUS_ACTIVE);
        User user2 = getCustomMockUser(2L, "jane.doe@example.com","Jane", "Doe",  "Engineering", Constants.USER_STATUS_INACTIVE);
        UserResponseDTO dto1 = getCustomMockUserResponseDTO(1L, "john.doe@example.com","John", "Doe",  "Marketing", Constants.USER_STATUS_ACTIVE);
        UserResponseDTO dto2 = getCustomMockUserResponseDTO(2L, "jane.doe@example.com","Jane", "Doe",  "Engineering", Constants.USER_STATUS_INACTIVE);

        when(roleRepository.findByName(RoleConstants.ROLE_USER)).thenReturn(role);
        when(userRepository.findAllByRole(any(Role.class))).thenReturn(List.of(user1, user2));
        when(userMapper.toUserResponseDTO(user2, null)).thenReturn(dto2);

        Optional<String> status = Optional.of(Constants.USER_STATUS_INACTIVE);
        List<UserResponseDTO> result = userService.getUsersByRole(role.getName(), status);

        verify(userRepository).findAllByRole(role);
        verify(userMapper, never()).toUserResponseDTO(user1, null);
        verify(userMapper).toUserResponseDTO(user2, null);
        assertFalse(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void getUsersByRole_WithNoStatus_ReturnsAllUsers() {
        Role role = getRole();
        User user1 = getCustomMockUser(1L, "john.doe@example.com","John", "Doe", "Marketing", Constants.USER_STATUS_ACTIVE);
        User user2 = getCustomMockUser(2L, "jane.doe@example.com","Jane", "Doe",  "Engineering", Constants.USER_STATUS_INACTIVE);
        UserResponseDTO dto1 = getCustomMockUserResponseDTO(1L, "john.doe@example.com","John", "Doe",  "Marketing", Constants.USER_STATUS_ACTIVE);
        UserResponseDTO dto2 = getCustomMockUserResponseDTO(2L, "jane.doe@example.com","Jane", "Doe",  "Engineering", Constants.USER_STATUS_INACTIVE);

        when(roleRepository.findByName(RoleConstants.ROLE_USER)).thenReturn(role);
        when(userRepository.findAllByRole(role)).thenReturn(List.of(user1, user2));
        when(userMapper.toUserResponseDTO(user1, null)).thenReturn(dto1);
        when(userMapper.toUserResponseDTO(user2, null)).thenReturn(dto2);

        Optional<String> status = Optional.empty();
        List<UserResponseDTO> result = userService.getUsersByRole(role.getName(), status);

        verify(userRepository).findAllByRole(role);
        verify(userMapper).toUserResponseDTO(user1, null);
        verify(userMapper).toUserResponseDTO(user2, null);
        assertTrue(result.containsAll(List.of(dto1, dto2)));
    }

    @Test
    void testAddUser_ExistingUser_Success() {
        User user = createUser();
        UserDTO requestDTO = getMockUserDTO();

        when(roleRepository.findByName(requestDTO.getRoleName())).thenReturn(getRole());
        when(userRepository.findById(requestDTO.getUuid())).thenReturn(Optional.of(user));
        when(userMapper.userDTOToUser(any(UserDTO.class), any(Role.class), anyString(), any(LocalDateTime.class)))
                .thenReturn(user);
        when(userMapper.toUserResponseDTO(any(User.class), anyString()))
                .thenReturn(getMockUserResponseDTO(Constants.USER_DETAILS_UPDATED));

        UserResponseDTO responseDTO = userService.addUser(requestDTO, "System");

        verify(userRepository).save(any(User.class));
        assertEquals(Constants.USER_DETAILS_UPDATED, responseDTO.getMessage());
    }

    @Test
    void testAddUser_NewUser_Success() {
        User user = createUser();
        UserDTO requestDTO = getMockUserDTO();

        when(userRepository.findById(requestDTO.getUuid())).thenReturn(Optional.empty());
        when(roleRepository.findByName(requestDTO.getRoleName())).thenReturn(getRole());
        when(userMapper.userDTOToUser(any(UserDTO.class), any(Role.class), anyString(), any(LocalDateTime.class)))
                .thenReturn(user);
        when(userMapper.toUserResponseDTO(any(User.class), eq(Constants.USER_ADDED)))
                .thenReturn(getMockUserResponseDTO(Constants.USER_ADDED));

        UserResponseDTO actualResponse = userService.addUser(requestDTO, "System");

        assertNotNull(actualResponse);
        assertEquals(Constants.USER_ADDED, actualResponse.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_ShouldSetInactiveStatusAndUpdateUser() {
        User user = createUser();
        user.setStatus(Constants.USER_STATUS_ACTIVE);
        UserResponseDTO responseDTO = getMockUserResponseDTO(Constants.USER_INACTIVE);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(any(User.class), eq(Constants.USER_INACTIVE))).thenReturn(responseDTO);

        UserResponseDTO result = userService.deactivateUser(1L, "System");

        assertEquals(Constants.USER_STATUS_INACTIVE, user.getStatus());
        verify(userRepository).save(user);
        verify(userMapper).toUserResponseDTO(user, Constants.USER_INACTIVE);
        assertEquals(responseDTO, result);
        assertEquals(Constants.USER_INACTIVE, result.getMessage());
    }

    @Test
    void deactivateUser_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.deactivateUser(1L, "System"));
        assertEquals(ErrorMessages.USER_NOT_FOUND_WITH_UID + 1, exception.getMessage());
    }
    @Test
    void testGetUsersByPractice_isEmpty() {
        Authentication auth = getMockAuthentication();
        String emptyPractice = "";
        User user = createUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()-> userService.getUsersByPractice(emptyPractice, auth));
        assertEquals(ErrorMessages.PRACTICE_NOT_NULL, exception.getMessage());
    }

    @Test
    void testGetUser_InvalidAuthentication_ThrowsAccessDeniedException() {
        assertThrows(AccessDeniedException.class, () -> userService.getUser((Authentication) null));

        Authentication invalidAuthentication = new TestingAuthenticationToken(null, null);
        assertThrows(AccessDeniedException.class, () -> userService.getUser(invalidAuthentication));
    }

    @Test
    void testGetUser_UserNotFound_ThrowsUserNotFoundException() {
        Authentication auth = getMockAuthentication();

        when(userRepository.findByEmail("test_user@epam.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUser(auth));
        assertEquals(ErrorMessages.USER_NOT_FOUND_WITH_EMAIL + "test_user@epam.com", exception.getMessage());
    }


    @Test
    void testGetUser_InvalidPrincipalType_ThrowsAccessDeniedException() {

        Authentication invalidAuthentication = new TestingAuthenticationToken(new Object(), null);

        Exception exception = assertThrows(AccessDeniedException.class, () -> userService.getUser(invalidAuthentication));
        assertEquals(ErrorMessages.ACCESS_DENIED, exception.getMessage());
    }

    @Test
    void testGetUsersByPractice_UserInactive_ThrowsAccessDeniedException() {
        Authentication auth = getMockAuthentication();
        String practice = "java";
        User inactiveUser = createUser();
        inactiveUser.setStatus(Constants.USER_STATUS_INACTIVE);

        when(userRepository.findByEmail("test_user@epam.com")).thenReturn(Optional.of(inactiveUser));

        Exception exception = assertThrows(AccessDeniedException.class, () -> userService.getUsersByPractice(practice, auth));
        assertEquals(ErrorMessages.ACCESS_DENIED, exception.getMessage());
    }

    @Test
    void testGetUuidByEmailExistingUser() {
        User user = createUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Long uuid = userService.getUuidByEmail(user.getEmail());
        assertEquals(user.getUuid(), uuid);
    }

    @Test
    void testGetUuidByEmailUserNotFound() {
        String email = "nonexistent_user@epam.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUuidByEmail(email));
        assertEquals("User not found with email " + email, exception.getMessage());
    }
    @Test
    void testGetUsersWithPracticeHeadRole() {
        Role practiceRole = Role.builder().name(RoleConstants.PRACTICE).build();
        User user1 = getCustomMockUser(1L, "john.doe@example.com", "John", "Doe", "Java", Constants.USER_STATUS_ACTIVE);
        user1.setDelegate(false);
        User user2 = getCustomMockUser(2L, "jane.delegation@example.com", "Jane", "Delegation", "Python", Constants.USER_STATUS_ACTIVE);
        user2.setDelegate(true);

        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(practiceRole);
        when(userRepository.findAllByRole(practiceRole)).thenReturn(List.of(user1, user2));

        List<UserDTO> result = userService.getUsersWithPracticeHeadRole();

        assertEquals(1, result.size());
        assertEquals(user1.getEmail(), result.get(0).getEmail());
        assertNotEquals(user2.getEmail(), result.get(0).getEmail());
    }
    @Test
    void testGetPracticeHeadByCompetency() {
        String competency = "Java";
        Role practiceRole = Role.builder().name(RoleConstants.PRACTICE).build();
        User user = getCustomMockUser(1L, competency, "John", "Doe", competency, Constants.USER_STATUS_ACTIVE);
        user.setRole(practiceRole);

        when(userRepository.findByPracticeAndRoleAndIsDelegate(competency, practiceRole, Boolean.FALSE))
                .thenReturn(Optional.of(user));
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(practiceRole);
        when(userMapper.toUserDTO(user)).thenReturn(getUserDTO());

        UserDTO result = userService.getPracticeHeadByCompetency(competency);

        assertNotNull(result);
        assertEquals("test_user@epam.com", result.getEmail());
    }

    @Test
    void testGetPracticeHeadByCompetencyNotFound() {
        String competency = "NonexistentCompetency";
        Role practiceRole = Role.builder().name(RoleConstants.PRACTICE).build();

        when(userRepository.findByPracticeAndRoleAndIsDelegate(competency, practiceRole, Boolean.FALSE))
                .thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(practiceRole);

        Exception exception = assertThrows(PracticeDelegationException.class,
                () -> userService.getPracticeHeadByCompetency(competency));
        assertEquals(ErrorMessages.PRACTICE_HEAD_NOT_FOUND, exception.getMessage());
    }


    public User createUser() {

        return User.builder()
                .uuid(1L)
                .email("test_user@epam.com")
                .firstName("Test")
                .lastName("User")
                .practice("java")
                .status("Active")
                .role(getRole())
                .build();
    }


    private Role getRole()
    {
        return Role.builder()
                .id(1L)
                .name(RoleConstants.ROLE_USER)
                .build();
    }

    private User getCustomMockUser(Long uid, String email, String firstName, String lastName, String practice, String status)
    {
        return User.builder()
                .uuid(uid)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .practice(practice)
                .role(getRole())
                .status(status)
                .build();
    }

    private UserResponseDTO getCustomMockUserResponseDTO(Long uid, String email, String firstName, String lastName, String practice, String status)
    {
        return UserResponseDTO.builder()
                .uuid(uid)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .practice(practice)
                .roleName(getRole().getName())
                .status(status)
                .build();
    }

    private UserResponseDTO getMockUserResponseDTO(String msg)
    {
        return UserResponseDTO.builder()
                .uuid(1L)
                .email("test_user@epam.com")
                .firstName("Test")
                .lastName("User")
                .practice("java")
                .roleName(RoleConstants.ROLE_USER)
                .status(Constants.USER_STATUS_ACTIVE)
                .message(msg)
                .build();
    }

    private UserDTO getMockUserDTO()
    {
        return UserDTO.builder()
                .uuid(1L)
                .email("test_user@epam.com")
                .firstName("Test")
                .lastName("User")
                .practice("java")
                .roleName(RoleConstants.ROLE_USER)
                .build();
    }



}