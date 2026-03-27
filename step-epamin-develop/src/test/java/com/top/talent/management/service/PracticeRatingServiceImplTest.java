package com.top.talent.management.service;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.CategoryRequestDTO;
import com.top.talent.management.dto.EmployeeRatingRequestDTO;
import com.top.talent.management.dto.EmployeeRatingResponseDTO;
import com.top.talent.management.dto.PracticeRatingResponseDTO;
import com.top.talent.management.dto.SubCategoryRatingDTO;
import com.top.talent.management.entity.Category;
import com.top.talent.management.entity.EmployeeRating;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.SubCategory;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.PracticeRatingException;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.repository.PracticeRatingRepository;
import com.top.talent.management.repository.SubCategoryRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.PracticeRatingServiceImpl;
import com.top.talent.management.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.top.talent.management.utils.TestUtils.getMockAuthentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PracticeRatingServiceImplTest {

    @Mock
    private ValidateCandidateService validateCandidateService;
    @Mock
    private PracticeRatingRepository practiceRatingRepository;
    @Mock
    private SubCategoryRepository subCategoryRepository;
    @Mock
    private MeanRatingService meanRatingService;
    @Mock
    private TopTalentEmployeeRepository topTalentEmployeeRepository;
    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TopTalentEmployeeMapper topTalentEmployeeMapper;
    @Mock
    private UserProfileServiceImpl userProfileService;
    @Mock
    private PracticeDelegateUserService practiceDelegateUserService;

    @InjectMocks
    private PracticeRatingServiceImpl practiceRatingService;

    private final long uid = 653000L;

    private final TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);

    @Test
    void testGetEmployeeRating_NoRatingsFound_EmptyList_Role_Practice() {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(validateCandidateService.isValidCandidate(eq(uid), any(CustomUserPrincipal.class), any(TopTalentExcelVersion.class))).thenReturn(getTopTalentEmployee());
        when(meanRatingService.getSubmissionStatus(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(SubmissionStatus.NA);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(Collections.emptyList());
        when(subCategoryRepository.findAll()).thenReturn(List.of(getMockSubCategory()));

        EmployeeRatingResponseDTO response = practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE));

        assertEquals(SubmissionStatus.NA, response.getStatus());
        assertEquals(0.0, response.getMean(), 0.0);
        assertNotNull(response.getCategories());
        assertNull(response.getMessage());
    }

    @Test
    void testGetEmployeeRating_RatingsFound_ButInDraft_Role_Practice() {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(validateCandidateService.isValidCandidate(eq(uid), any(CustomUserPrincipal.class), any(TopTalentExcelVersion.class))).thenReturn(getTopTalentEmployee());
        when(meanRatingService.getSubmissionStatus(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(SubmissionStatus.D);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(getEmployeeRatingList());

        EmployeeRatingResponseDTO response = practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE));

        assertEquals(SubmissionStatus.D, response.getStatus());
        assertEquals(0.0, response.getMean(), 0.0);
        assertNotNull(response.getCategories());
        assertEquals(Constants.DRAFT_STATUS, response.getMessage());
    }

    @Test
    void testGetEmployeeRating_RatingsFound_AndSubmitted_Role_Practice() {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(validateCandidateService.isValidCandidate(eq(uid), any(CustomUserPrincipal.class), any(TopTalentExcelVersion.class))).thenReturn(getTopTalentEmployee());
        when(meanRatingService.getSubmissionStatus(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(SubmissionStatus.S);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(getEmployeeRatingList());
        when(meanRatingService.getMeanRating(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(4.5);

        EmployeeRatingResponseDTO response = practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE));

        assertEquals(SubmissionStatus.S, response.getStatus());
        assertEquals(4.5, response.getMean(), 0.0);
        assertNotNull(response.getCategories());
        assertEquals(Constants.SUBMIT_STATUS, response.getMessage());

    }

    @Test
    void testGetEmployeeRating_NoRatingsFound_EmptyList_Role_SuperAdmin() {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(validateCandidateService.isValidCandidate(eq(uid), any(CustomUserPrincipal.class), any(TopTalentExcelVersion.class))).thenReturn(getTopTalentEmployee());
        when(meanRatingService.getSubmissionStatus(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(SubmissionStatus.NA);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(Collections.emptyList());
        when(subCategoryRepository.findAll()).thenReturn(List.of(getMockSubCategory()));

        EmployeeRatingResponseDTO response = practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal(RoleConstants.ROLE_SUPER_ADMIN));

        assertEquals(SubmissionStatus.NA, response.getStatus());
        assertEquals(0.0, response.getMean(), 0.0);
        assertNotNull(response.getCategories());
        assertNull(response.getMessage());
    }

    @Test
    void testGetEmployeeRating_RatingsFound_ButInDraft_Role_SuperAdmin() {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(validateCandidateService.isValidCandidate(eq(uid), any(CustomUserPrincipal.class), any(TopTalentExcelVersion.class))).thenReturn(getTopTalentEmployee());
        when(meanRatingService.getSubmissionStatus(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(SubmissionStatus.D);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(getEmployeeRatingList());

        EmployeeRatingResponseDTO response = practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal(RoleConstants.ROLE_SUPER_ADMIN));

        assertEquals(SubmissionStatus.D, response.getStatus());
        assertEquals(0.0, response.getMean(), 0.0);
        assertNotNull(response.getCategories());
        assertEquals(Constants.DRAFT_STATUS, response.getMessage());
    }

    @Test
    void testGetEmployeeRating_RatingsFound_AndSubmitted_Role_SuperAdmin() {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(validateCandidateService.isValidCandidate(eq(uid), any(CustomUserPrincipal.class), any(TopTalentExcelVersion.class))).thenReturn(getTopTalentEmployee());
        when(meanRatingService.getSubmissionStatus(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(SubmissionStatus.S);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(getEmployeeRatingList());
        when(meanRatingService.getMeanRating(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(4.5);

        EmployeeRatingResponseDTO response = practiceRatingService.getEmployeeRating(uid, getCustomUserPrincipal(RoleConstants.ROLE_SUPER_ADMIN));

        assertEquals(SubmissionStatus.S, response.getStatus());
        assertEquals(4.5, response.getMean(), 0.0);
        assertNotNull(response.getCategories());
        assertEquals(Constants.SUBMIT_STATUS, response.getMessage());

    }
    @Test
    void testSaveEmployeeRatings_SubmissionStatus_D_Role_Practice() {
        String mockEmail = "test_user@epam.com";
        long loggedInUid = 123456L;
        User mockUser = User.builder()
                .email(mockEmail)
                .uuid(loggedInUid)
                .practice("Java")
                .role(Role.builder().name(RoleConstants.ROLE_PRACTICE).build())
                .status(Constants.USER_STATUS_ACTIVE)
                .build();
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(mockUser));
        TopTalentExcelVersion version = TopTalentExcelVersion.builder()
                .uploadedYear("2023")
                .versionName("v1")
                .build();
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(version);
        when(validateCandidateService.isValidCandidate(eq(uid), any(CustomUserPrincipal.class), eq(version)))
                .thenReturn(getTopTalentEmployee());
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(uid), eq(version)))
                .thenReturn(getEmployeeRatingList());
        when(meanRatingService.getSubmissionStatus(eq(uid), eq(version))).thenReturn(SubmissionStatus.D);
        EmployeeRatingResponseDTO response = practiceRatingService.saveEmployeeRatings(
                uid, SubmissionStatus.D, getEmployeeRatingRequestDTO(), getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE)
        );
        assertEquals(SubmissionStatus.D, response.getStatus());
        assertEquals(0.0, response.getMean(), 0.0);
        assertEquals(Constants.DRAFT_STATUS, response.getMessage());
        verify(practiceRatingRepository, times(1)).save(any(EmployeeRating.class));
    }

    @Test
    void testSaveEmployeeRatings_SubmissionStatus_D_Role_SuperAdmin() {
        long differentUid = 123456L;
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(User.builder()
                .email("test_user@epam.com")
                .uuid(uid)
                .practice("Java")
                .role(Role.builder().name(RoleConstants.ROLE_SUPER_ADMIN).build())
                .build()));
        when(validateCandidateService.isValidCandidate(eq(differentUid), any(CustomUserPrincipal.class), eq(latestVersion)))
                .thenReturn(getTopTalentEmployee());
        when(meanRatingService.getSubmissionStatus(eq(differentUid), eq(latestVersion))).thenReturn(SubmissionStatus.D);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(differentUid), eq(latestVersion)))
                .thenReturn(getEmployeeRatingList());
        EmployeeRatingResponseDTO response = practiceRatingService.saveEmployeeRatings(
                differentUid,
                SubmissionStatus.D,
                getEmployeeRatingRequestDTO(),
                getCustomUserPrincipal(RoleConstants.ROLE_SUPER_ADMIN)
        );
        assertEquals(SubmissionStatus.D, response.getStatus());
        assertEquals(0.0, response.getMean(), 0.0);
        assertEquals(Constants.DRAFT_STATUS, response.getMessage());
        verify(practiceRatingRepository, times(1)).save(any(EmployeeRating.class));
    }

    @Test
    void testSaveEmployeeRatings_SubmissionStatus_S_Role_Practice() {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(userRepository.findByEmail("test_user@epam.com")).thenReturn(Optional.of(User.builder()
                .email("test_user@epam.com")
                .uuid(12345L)
                .practice("Java")
                .role(Role.builder().name(RoleConstants.ROLE_PRACTICE).build())
                .status(Constants.USER_STATUS_ACTIVE)
                .build()));
        when(validateCandidateService.isValidCandidate(eq(uid), any(CustomUserPrincipal.class), any(TopTalentExcelVersion.class))).thenReturn(getTopTalentEmployee());
        when(meanRatingService.getSubmissionStatus(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(SubmissionStatus.S);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(getEmployeeRatingList());
        when(meanRatingService.getMeanRating(eq(uid), any(TopTalentExcelVersion.class))).thenReturn(4.5);

        EmployeeRatingResponseDTO response = practiceRatingService.saveEmployeeRatings(uid, SubmissionStatus.S, getEmployeeRatingRequestDTO(), getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE));

        assertEquals(SubmissionStatus.S, response.getStatus());
        assertEquals(4.5, response.getMean(), 0.0);
        assertEquals(Constants.SUBMIT_STATUS, response.getMessage());

        verify(meanRatingService, times(1)).calculateMeanRating(eq(uid), anyString(), eq(SubmissionStatus.S), any(TopTalentExcelVersion.class));
    }

    @Test
    void testSaveEmployeeRatings_SubmissionStatus_S_Role_SuperAdmin() {

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(userRepository.findByEmail("test_user@epam.com")).thenReturn(Optional.of(User.builder()
                .email("test_user@epam.com")
                .uuid(12345L)
                .practice("All Practices")
                .role(Role.builder().name(RoleConstants.ROLE_SUPER_ADMIN).build())
                .status(Constants.USER_STATUS_ACTIVE)
                .build()));
        when(validateCandidateService.isValidCandidate(eq(uid), any(CustomUserPrincipal.class), eq(latestVersion)))
                .thenReturn(getTopTalentEmployee());
        when(meanRatingService.getSubmissionStatus(eq(uid), eq(latestVersion))).thenReturn(SubmissionStatus.S);
        when(meanRatingService.getMeanRating(eq(uid), eq(latestVersion))).thenReturn(4.5);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(eq(uid), eq(latestVersion)))
                .thenReturn(getEmployeeRatingList());
        EmployeeRatingResponseDTO response = practiceRatingService.saveEmployeeRatings(
                uid, SubmissionStatus.S, getEmployeeRatingRequestDTO(), getCustomUserPrincipal(RoleConstants.ROLE_SUPER_ADMIN)
        );
        assertEquals(SubmissionStatus.S, response.getStatus());
        assertEquals(4.5, response.getMean(), 0.0);
        assertEquals(Constants.SUBMIT_STATUS, response.getMessage());
        verify(meanRatingService, times(1)).calculateMeanRating(eq(uid), anyString(), eq(SubmissionStatus.S), eq(latestVersion));
    }

    @Test
    void shouldThrowExceptionWhenDelegateTriesToApproveWithoutPermission() {
        Authentication authentication = getMockAuthentication(RoleConstants.ROLE_PRACTICE);
        CustomUserPrincipal delegatePrincipal = (CustomUserPrincipal) authentication.getPrincipal();
        delegatePrincipal.setDelegate(true);
        SubmissionStatus submissionStatus = SubmissionStatus.A;
        EmployeeRatingRequestDTO requestDTO = EmployeeRatingRequestDTO.builder().build();

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(practiceDelegateUserService.isApprovalRequired()).thenReturn(true);
        when(userRepository.findByEmail(delegatePrincipal.getEmail())).thenReturn(Optional.of(User.builder()
                .email(delegatePrincipal.getEmail())
                .uuid(12345L)
                .practice("Java")
                .role(Role.builder().name(RoleConstants.ROLE_PRACTICE).build())
                .build()));

        Exception exception = assertThrows(RuntimeException.class, () ->
                practiceRatingService.saveEmployeeRatings(uid, submissionStatus, requestDTO, delegatePrincipal)
        );

        assertEquals(ErrorMessages.PRACTICE_RATING_NO_APPROVAL_PERMISSION, exception.getMessage());
    }

    @Test
    void testGetCompetencies() {
        List<String> expectedCompetencies = List.of("Java", "Python", "SQL");

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(topTalentEmployeeRepository.findDistinctCompetencyPracticesByVersion(latestVersion)).thenReturn(expectedCompetencies);

        List<String> actualCompetencies = practiceRatingService.getCompetencies();

        assertNotNull(actualCompetencies);
        assertEquals(expectedCompetencies, actualCompetencies);
        verify(topTalentEmployeeRepository, times(1)).findDistinctCompetencyPracticesByVersion(latestVersion);
    }

    @Test
    void testGetCompetenciesReturnsEmpty() {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(topTalentEmployeeRepository.findDistinctCompetencyPracticesByVersion(latestVersion))
                .thenReturn(List.of());

        List<String> actualCompetencies = practiceRatingService.getCompetencies();

        assertTrue(actualCompetencies.isEmpty());
        verify(topTalentEmployeeRepository, times(1)).findDistinctCompetencyPracticesByVersion(any(TopTalentExcelVersion.class));
    }

    @Test
    void testGetCandidates_WithCompetency_Role_Practice() {
        String email = "test@example.com";
        String competency = "Java";

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(getUser(RoleConstants.ROLE_PRACTICE)));
        when(topTalentEmployeeRepository.findByCompetencyPracticeAndTopTalentExcelVersion(anyString(), any(TopTalentExcelVersion.class))).thenReturn(getMockEmployees());

        PracticeRatingResponseDTO response = practiceRatingService.getCandidates(email, competency);

        assertNotNull(response);
        assertEquals("Java", response.getCompetency());
        assertNotNull(response.getUsers());
        assertFalse(response.getUsers().isEmpty());

        verify(userRepository, times(1)).findByEmail(email);
        verify(topTalentEmployeeRepository, times(1))
                .findByCompetencyPracticeAndTopTalentExcelVersion(anyString(), any(TopTalentExcelVersion.class));
    }

    @Test
    void testGetCandidates_DefaultCompetency_Role_SuperAdmin() {
        String email = "test@example.com";
        String competency = "All";

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(getUser(RoleConstants.ROLE_SUPER_ADMIN)));
        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(latestVersion)).thenReturn(getMockEmployees());

        PracticeRatingResponseDTO response = practiceRatingService.getCandidates(email, competency);

        assertNotNull(response);
        assertEquals(competency, response.getCompetency());

        verify(userRepository, times(1)).findByEmail(email);
        verify(topTalentEmployeeRepository, times(1)).findAllByTopTalentExcelVersion(any(TopTalentExcelVersion.class));
    }

    @Test
    void testGetCandidates_WithCompetency_Role_SuperAdmin() {
        String email = "test@example.com";
        String competency = "Java";

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(getUser(RoleConstants.ROLE_SUPER_ADMIN)));
        when(topTalentEmployeeRepository.findByCompetencyPracticeAndTopTalentExcelVersion(anyString(), any(TopTalentExcelVersion.class))).thenReturn(getMockEmployees());

        PracticeRatingResponseDTO response = practiceRatingService.getCandidates(email, competency);

        assertNotNull(response);
        assertEquals("Java", response.getCompetency());
        assertNotNull(response.getUsers());
        assertFalse(response.getUsers().isEmpty());

        verify(userRepository, times(1)).findByEmail(email);
        verify(topTalentEmployeeRepository, times(1))
                .findByCompetencyPracticeAndTopTalentExcelVersion(anyString(), any(TopTalentExcelVersion.class));
    }

    @Test
    void testApproveAllEmployeesRatings_Success() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User mockUser = getUser(RoleConstants.ROLE_PRACTICE);

        when(CustomUserPrincipal.getLoggedInUser()).thenReturn(getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(meanRatingService.approveAllMeanRatings(latestVersion, mockUser.getPractice())).thenReturn(true);

        boolean result = practiceRatingService.approveAllEmployeesRatings();

        assertTrue(result);
        verify(meanRatingService).approveAllMeanRatings(latestVersion, "Java");
        SecurityContextHolder.clearContext();
    }

    @Test
    void testApproveAllEmployeesRatings_Failure() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User mockUser = getUser(RoleConstants.ROLE_PRACTICE);

        when(CustomUserPrincipal.getLoggedInUser()).thenReturn(getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(meanRatingService.approveAllMeanRatings(latestVersion, mockUser.getPractice())).thenReturn(false);

        boolean result = practiceRatingService.approveAllEmployeesRatings();

        assertFalse(result);
        verify(meanRatingService).approveAllMeanRatings(latestVersion, "Java");
        SecurityContextHolder.clearContext();
    }
    @Test
    void shouldThrowExceptionWhenPracticeHeadAttemptsSelfRating() {
        String mockEmail = "test_user@epam.com";
        long userPracticeHeadUid = 653000L;
        CustomUserPrincipal mockPrincipal = getCustomUserPrincipal(RoleConstants.ROLE_PRACTICE);
        User practiceHead = User.builder()
                .email(mockEmail)
                .uuid(userPracticeHeadUid)
                .practice("Java")
                .role(Role.builder().name(RoleConstants.ROLE_PRACTICE).build())
                .build();
        when(userRepository.findByEmail(mockEmail)).thenReturn(Optional.of(practiceHead));
        when(validateCandidateService.isValidCandidate(eq(userPracticeHeadUid), eq(mockPrincipal), any(TopTalentExcelVersion.class)))
                .thenReturn(getTopTalentEmployee());
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        Exception exception = assertThrows(PracticeRatingException.class, () ->
                practiceRatingService.saveEmployeeRatings(
                        userPracticeHeadUid, // Same UID for self-rating
                        SubmissionStatus.D,
                        getEmployeeRatingRequestDTO(),
                        mockPrincipal
                )
        );
        assertEquals(ErrorMessages.SELF_RATING_NOT_ALLOWED, exception.getMessage());
    }

    private CustomUserPrincipal getCustomUserPrincipal(String role)
    {
        Authentication authenticationToken = getMockAuthentication(role);

        return (CustomUserPrincipal) authenticationToken.getPrincipal();
    }

    private User getUser(String roleName)
    {
        return User.builder()
                .practice("Java")
                .role(getRole(roleName))
                .status(Constants.USER_STATUS_ACTIVE)
                .build();
    }

    private Role getRole(String roleName)
    {
        return Role.builder()
                .name(roleName)
                .build();
    }

    private List<TopTalentEmployee> getMockEmployees()
    {
        TopTalentEmployee employee1 = TopTalentEmployee.builder()
                .uid(1L)
                .name("John Doe")
                .title("Developer")
                .primarySkill("Java")
                .talentProfile("Expert")
                .talentProfilePreviousYear("Proficient")
                .build();

        TopTalentEmployee employee2 = TopTalentEmployee.builder()
                .uid(2L)
                .name("Jane Smith")
                .title("Senior Developer")
                .primarySkill("Spring Boot")
                .talentProfile("Expert")
                .talentProfilePreviousYear("Proficient")
                .build();

        return List.of(employee1, employee2);
    }

    private Category getMockCategory()
    {
        return Category.builder()
                .categoryId(1L)
                .categoryName("category1")
                .build();
    }

    private SubCategory getMockSubCategory()
    {
        return SubCategory.builder()
                .subCategoryId(1L)
                .subCategoryName("subcategory1")
                .category(getMockCategory())
                .build();
    }

    private TopTalentEmployee getTopTalentEmployee()
    {
        return TopTalentEmployee.builder()
                .uid(uid)
                .competencyPractice("java")
                .primarySkill("java")
                .name("John Doe")
                .title("Engineer")
                .build();
    }

    private List<EmployeeRating> getEmployeeRatingList()
    {
        EmployeeRating rating = EmployeeRating.builder()
                .uid(uid)
                .subCategory(getMockSubCategory())
                .build();

        List<EmployeeRating> ratings = new ArrayList<>();
        ratings.add(rating);

        return ratings;
    }

    private EmployeeRatingRequestDTO getEmployeeRatingRequestDTO()
    {
        SubCategoryRatingDTO subCategoryRatingDTO = SubCategoryRatingDTO.builder()
                .subCategoryName("SubCategory1")
                .employeeRating(3.0)
                .build();

        CategoryRequestDTO categoryRequest = CategoryRequestDTO.builder()
                .subCategory(Collections.singletonList(subCategoryRatingDTO))
                .build();

        return EmployeeRatingRequestDTO.builder()
                .categories(Collections.singletonList(categoryRequest))
                .build();
    }
}