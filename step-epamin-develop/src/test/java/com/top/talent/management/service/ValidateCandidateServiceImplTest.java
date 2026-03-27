package com.top.talent.management.service;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.PracticeRatingException;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.ValidateCandidateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateCandidateServiceImplTest {

    @Mock
    private TopTalentEmployeeRepository topTalentEmployeeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ValidateCandidateServiceImpl validateCandidateService;

    private final long uid = 653000L;

    private final TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);

    @Test
    void isValidCandidate_ValidCandidate_Role_SuperAdmin() {
        CustomUserPrincipal customUserPrincipal = getCustomUserPrincipal(RoleConstants.ROLE_SUPER_ADMIN);
        TopTalentEmployee employee = getTopTalentEmployee();

        when(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(anyLong(), any(TopTalentExcelVersion.class))).thenReturn(Optional.of(employee));
        when(userRepository.findByEmail(customUserPrincipal.getEmail())).thenReturn(Optional.of(getUser(null, "SA")));

        TopTalentEmployee resultEmployee = assertDoesNotThrow(() -> validateCandidateService.isValidCandidate(uid, customUserPrincipal, latestVersion));
        assertEquals(employee, resultEmployee);
    }

    @Test
    void isValidCandidate_ValidCandidate_Role_Practice() {
        CustomUserPrincipal customUserPrincipal = getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE);
        TopTalentEmployee employee = getTopTalentEmployee();

        when(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(anyLong(), any(TopTalentExcelVersion.class))).thenReturn(Optional.of(employee));
        when(userRepository.findByEmail(customUserPrincipal.getEmail())).thenReturn(Optional.of(getUser("Engineering", RoleConstants.ROLE_PRACTICE)));

        TopTalentEmployee resultEmployee = assertDoesNotThrow(() -> validateCandidateService.isValidCandidate(uid, customUserPrincipal, latestVersion));
        assertEquals(employee, resultEmployee);
    }

    @Test
    void isValidCandidate_InvalidCandidate_TopTalentEmployeeDoesNotExist() {
        CustomUserPrincipal customUserPrincipal = getCustomUserPrincipal(null);

        when(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(anyLong(), any(TopTalentExcelVersion.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(PracticeRatingException.class, () -> validateCandidateService.isValidCandidate(uid, customUserPrincipal, latestVersion));
        assertEquals(ErrorMessages.CANDIDATE_DOES_NOT_EXIST, exception.getMessage());
    }

    @Test
    void isValidCandidate_InvalidCandidate_PracticeDoesNotMatch() {
        CustomUserPrincipal customUserPrincipal = getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE);

        when(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(anyLong(), any(TopTalentExcelVersion.class))).thenReturn(Optional.of(getTopTalentEmployee()));
        when(userRepository.findByEmail(customUserPrincipal.getEmail())).thenReturn(Optional.of(getUser("Marketing", RoleConstants.ROLE_PRACTICE)));

        Exception exception = assertThrows(PracticeRatingException.class, () -> validateCandidateService.isValidCandidate(uid, customUserPrincipal, latestVersion));
        assertEquals(ErrorMessages.INVALID_CANDIDATE, exception.getMessage());
    }

    private CustomUserPrincipal getCustomUserPrincipal(String role)
    {
        return new CustomUserPrincipal("John", "Doe", "john_doe@example.com", role, false);
    }

    private TopTalentEmployee getTopTalentEmployee()
    {
        return TopTalentEmployee.builder()
                .uid(uid)
                .competencyPractice("Engineering")
                .build();
    }

    private User getUser(String practice, String role)
    {
        return User.builder()
                .practice(practice)
                .role(getRole(role))
                .build();
    }

    private Role getRole(String role)
    {
        return Role.builder()
                .name(role)
                .build();
    }
}