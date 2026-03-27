package com.top.talent.management.helper;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.User;
import com.top.talent.management.mapper.UserMapper;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuperAdminServiceHelperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SuperAdminServiceHelper superAdminServiceHelper;

    @Mock
    private RoleRepository roleRepository;

    public SuperAdminServiceHelperTest(){
        TestUtils.getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);
    }

    @Test
    void generateUserObject_Success() {

        TopTalentEmployee topTalentEmployee = new TopTalentEmployee();
        topTalentEmployee.setName("John Doe");

        User mappedUser = new User();
        when(userMapper.toUser(topTalentEmployee)).thenReturn(mappedUser);
        when(roleRepository.findByName("U")).thenReturn(new Role(3L,"U"));
        User user = superAdminServiceHelper.generateUserObject(topTalentEmployee);


        assertNotNull(user);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Active", user.getStatus());
        assertEquals(3L, user.getRole().getId());
        assertEquals("John Doe", user.getCreatedBy());
        assertEquals("John Doe", user.getLastUpdatedBy());
        assertNotNull(user.getCreated());
        assertNotNull(user.getLastUpdated());

        verify(userMapper, times(1)).toUser(topTalentEmployee);
    }

    @Test
    void generateUserObject_SingleName() {

        TopTalentEmployee topTalentEmployee = new TopTalentEmployee();
        topTalentEmployee.setName("John");

        User mappedUser = new User();
        when(userMapper.toUser(topTalentEmployee)).thenReturn(mappedUser);
        when(roleRepository.findByName("U")).thenReturn(new Role(3L,"U"));

        User user = superAdminServiceHelper.generateUserObject(topTalentEmployee);

        assertNotNull(user);
        assertEquals("John", user.getFirstName());
        assertEquals("", user.getLastName());
        assertEquals("Active", user.getStatus());
        assertEquals(3L, user.getRole().getId());
        assertEquals("John Doe", user.getCreatedBy());
        assertEquals("John Doe", user.getLastUpdatedBy());
        assertNotNull(user.getCreated());
        assertNotNull(user.getLastUpdated());

        verify(userMapper, times(1)).toUser(topTalentEmployee);
    }

    @ParameterizedTest
    @CsvSource({
            "Alice Smith, Alice, Smith",    // Full name
            "Alice, Alice, ''",     // Single name
            "'', '', ''"    // Empty name
    })
    void testGetFirstAndLastName(String fullName, String expectedFirstName, String expectedLastName) {
        String[] result = superAdminServiceHelper.getFirstAndLastName(fullName);
        assertEquals(expectedFirstName, result[0]);
        assertEquals(expectedLastName, result[1]);
    }

}
