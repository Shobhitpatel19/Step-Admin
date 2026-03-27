package com.top.talent.management.service;

import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.mapper.UserMapper;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.service.impl.SuperAdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.top.talent.management.constants.Constants.EMPTY_USER_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuperAdminServiceImplTest {

    @Mock
    private TopTalentEmployeeRepository topTalentEmployeeRepository;

    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;

    @Mock
    private UserService userService;


    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private SuperAdminServiceImpl superAdminService;



    @Test
    void grantAccessToUserRole_Success() {

        TopTalentExcelVersion excelVersion = new TopTalentExcelVersion();

        TopTalentEmployee employee1 = new TopTalentEmployee();
        employee1.setUid(1L);
        employee1.setTopTalentExcelVersion(excelVersion);
        TopTalentEmployee employee2 = new TopTalentEmployee();
        employee2.setUid(2L);
        employee1.setTopTalentExcelVersion(excelVersion);

        Role role = new Role();
        role.setId(3L);

        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setFirstName("User");
        user1.setLastName("One");
        user1.setPractice("Microsoft");
        user1.setRole(role);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setPractice("Microsoft");
        user1.setPractice("Microsoft");
        user1.setRole(role);

        when(topTalentExcelVersionService.findLatestVersion())
                .thenReturn(excelVersion);

        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersionAndIsStepUser(excelVersion, true))
                .thenReturn(Arrays.asList(employee1, employee2));
        when(userService.addEligibleUsers(Arrays.asList(employee1, employee2)))
                .thenReturn(Arrays.asList(user1, user2));
        when(userMapper.toUserDTO(any(User.class))).thenReturn(UserDTO.builder().build());

        Mockito.lenient().when(userRepository.findAllByRole(any())).thenReturn(Arrays.asList(user1, user2));

        List<UserDTO> result = superAdminService.grantAccessToUserRole();

        assertNotNull(result);
        assertEquals(2, result.size());

    }

    @Test
    void grantAccessToUserRole_EmptyStepUserList() {

        TopTalentExcelVersion excelVersion = new TopTalentExcelVersion();
        excelVersion.setCreated(LocalDateTime.now());

        when(topTalentExcelVersionService.findLatestVersion())
                .thenReturn(excelVersion);
        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersionAndIsStepUser(excelVersion, true))
                .thenReturn(Collections.emptyList());
        List<UserDTO> userDTOS=superAdminService.grantAccessToUserRole();

        assertEquals(EMPTY_USER_LIST, userDTOS);
    }
}