package com.top.talent.management.service;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.PracticeDelegationFeaturesConstants;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.FutureSkillPracticeDTO;
import com.top.talent.management.dto.FutureSkillRequestDTO;
import com.top.talent.management.dto.FutureSkillRequestListDTO;
import com.top.talent.management.dto.FutureSkillResponseDTO;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserResponseDTO;
import com.top.talent.management.entity.FutureSkill;
import com.top.talent.management.entity.FutureSkillCategory;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.FutureSkillException;
import com.top.talent.management.repository.FutureSkillCategoryRepository;
import com.top.talent.management.repository.FutureSkillRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.FutureSkillServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@ExtendWith(MockitoExtension.class)
class FutureSkillServiceImplTest {

    @Mock
    private FutureSkillRepository futureSkillRepository;

    @Mock
    private FutureSkillCategoryRepository futureSkillCategoryRepository;

    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Mock
    private MailGenerationService mailGenerationService;

    @Mock
    private PracticeDelegateUserService practiceDelegateUserService;

    @Mock
    private IdentificationClosureService identificationClosureService;

    @InjectMocks
    private FutureSkillServiceImpl futureSkillService;

    private final CustomUserPrincipal userPrincipal =  new CustomUserPrincipal("John", "Doe", "test@example.com", RoleConstants.PRACTICE, false);


    @Test
    void testGetFutureSkill_WhenLatestVersionPresent() {
        UserDTO userDTO = getUserDTO();
        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        FutureSkill futureSkill1 = FutureSkill.builder()
                .practiceName("IT")
                .submissionStatus(SubmissionStatus.S)
                .lastUpdated(LocalDateTime.now())
                .futureSkillCategory(FutureSkillCategory.builder().id(2L).categoryName("Category2").build())
                .build();
        FutureSkill futureSkill2 = FutureSkill.builder()
                .practiceName("IT")
                .submissionStatus(SubmissionStatus.S)
                .lastUpdated(LocalDateTime.now())
                .futureSkillCategory(FutureSkillCategory.builder().id(1L).categoryName("Category1").build())
                .build();

        when(userService.getUser(userPrincipal.getEmail())).thenReturn(userDTO);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(futureSkillRepository.existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                 "IT", latestVersion, false)).thenReturn(true);
        when(futureSkillRepository.findByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                 "IT", latestVersion, false)).thenReturn(new ArrayList<>(List.of(futureSkill1, futureSkill2)));

        FutureSkillResponseDTO response = futureSkillService.getFutureSkill(userPrincipal);

        assertEquals("John Doe", response.getPracticeHeadName());
        assertEquals("IT", response.getPracticeName());
        assertEquals(SubmissionStatus.S, response.getSubmissionStatus());
        assertNotNull(response.getCategories());
        assertEquals(2, response.getCategories().size());
        assertEquals("Category1", response.getCategories().get(0).getCategoryName());
        assertEquals("Category2", response.getCategories().get(1).getCategoryName());
    }

    @Test
    void testGetFutureSkill_WhenLatestVersionPresent_ForRoleUser() {
        CustomUserPrincipal userPrincipal1 = new CustomUserPrincipal(
                "Candidate",
                "User",
                "candidate@epam.com",
                RoleConstants.ROLE_USER,
                false
        );

        String practice = "Java Practice";
        UserDTO candidateUser = new UserDTO();
        candidateUser.setFirstName("Candidate");
        candidateUser.setLastName("User");
        candidateUser.setEmail("candidate@epam.com");
        candidateUser.setPractice(practice);
        when(userService.getUser(userPrincipal1.getEmail())).thenReturn(candidateUser);

        UserDTO practiceHead = new UserDTO();
        practiceHead.setFirstName("John");
        practiceHead.setLastName("Doe");
        practiceHead.setPractice(practice);
        when(userService.getPracticeHeadByCompetency(practice)).thenReturn(practiceHead);

        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        latestVersion.setVersionName("2023");
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);

        when(futureSkillRepository.existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                 practice, latestVersion, false)).thenReturn(true);

        FutureSkill futureSkill1 = FutureSkill.builder()
                .practiceName(practice)
                .submissionStatus(SubmissionStatus.S)
                .lastUpdated(LocalDateTime.now())
                .futureSkillCategory(FutureSkillCategory.builder().id(2L).categoryName("Category2").build())
                .build();
        FutureSkill futureSkill2 = FutureSkill.builder()
                .practiceName(practice)
                .submissionStatus(SubmissionStatus.S)
                .lastUpdated(LocalDateTime.now())
                .futureSkillCategory(FutureSkillCategory.builder().id(1L).categoryName("Category1").build())
                .build();

        when(futureSkillRepository.findByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                 practice, latestVersion, false)).thenReturn(new ArrayList<>(List.of(futureSkill1, futureSkill2)));

        FutureSkillResponseDTO response = futureSkillService.getFutureSkill(userPrincipal1);

        assertEquals("John Doe", response.getPracticeHeadName(), "Practice Head name must match the fetched Practice Head");
        assertEquals(practice, response.getPracticeName(), "Practice name must match the user's practice");
        assertEquals(SubmissionStatus.S, response.getSubmissionStatus(), "Submission status must match mock data");
        assertNotNull(response.getCategories(), "Categories must not be null");
        assertEquals(2, response.getCategories().size(), "Two categories must be returned");
        assertEquals("Category1", response.getCategories().get(0).getCategoryName(), "Categories must be sorted by ID");
        assertEquals("Category2", response.getCategories().get(1).getCategoryName(), "Categories must be sorted by ID");

        verify(userService).getPracticeHeadByCompetency(practice);
        verify(futureSkillRepository).existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                practice, latestVersion, false);
        verify(futureSkillRepository).findByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                practice, latestVersion, false);
    }

    @Test
    void testGetFutureSkill_WhenPreviousVersionPresent() {
        UserDTO userDTO = getUserDTO();
        TopTalentExcelVersion previousVersion = new TopTalentExcelVersion();
        FutureSkill futureSkill1 = FutureSkill.builder()
                .practiceName("IT")
                .submissionStatus(SubmissionStatus.A)
                .lastUpdated(LocalDateTime.now())
                .futureSkillCategory(FutureSkillCategory.builder().id(3L).categoryName("Category3").build())
                .build();
        FutureSkill futureSkill2 = FutureSkill.builder()

                .practiceName("IT")
                .submissionStatus(SubmissionStatus.A)
                .lastUpdated(LocalDateTime.now())
                .futureSkillCategory(FutureSkillCategory.builder().id(2L).categoryName("Category2").build())
                .build();

        when(userService.getUser(userPrincipal.getEmail())).thenReturn(userDTO);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(null);
        when(topTalentExcelVersionService.getPreviousYearVersion()).thenReturn(previousVersion);
        when(futureSkillRepository.existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                "IT", previousVersion, false)).thenReturn(true);
        when(futureSkillRepository.findByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                "IT", previousVersion, false)).thenReturn(new ArrayList<>(List.of(futureSkill1, futureSkill2)));

        FutureSkillResponseDTO response = futureSkillService.getFutureSkill(userPrincipal);

        assertEquals("John Doe", response.getPracticeHeadName());
        assertEquals("IT", response.getPracticeName());
        assertEquals(SubmissionStatus.A, response.getSubmissionStatus());
        assertNotNull(response.getCategories());
        assertEquals(2, response.getCategories().size());
        assertEquals("Category2", response.getCategories().get(0).getCategoryName());
        assertEquals("Category3", response.getCategories().get(1).getCategoryName());
    }


    @Test
    void testGetFutureSkill_WhenNoVersionPresent() {
        UserDTO userDTO = getUserDTO();
        TopTalentExcelVersion naVersion = TopTalentExcelVersion.builder()
                .versionName("NA")
                .build();

        FutureSkillCategory category1 = FutureSkillCategory.builder().id(1L).categoryName("DefaultCategory1").build();
        FutureSkillCategory category2 = FutureSkillCategory.builder().id(2L).categoryName("DefaultCategory2").build();

        when(userService.getUser(userPrincipal.getEmail())).thenReturn(userDTO);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(null);
        when(topTalentExcelVersionService.getPreviousYearVersion()).thenReturn(naVersion);
        when(futureSkillCategoryRepository.findAll(Sort.by("id"))).thenReturn(List.of(category1, category2));

        FutureSkillResponseDTO response = futureSkillService.getFutureSkill(userPrincipal);

        assertEquals("John Doe", response.getPracticeHeadName());
        assertEquals("IT", response.getPracticeName());
        assertEquals(SubmissionStatus.NA, response.getSubmissionStatus());
        assertNotNull(response.getCategories());
        assertEquals(2, response.getCategories().size());
        assertEquals("DefaultCategory1", response.getCategories().get(0).getCategoryName());
        assertEquals("DefaultCategory2", response.getCategories().get(1).getCategoryName());
    }

    @Test
    void testSaveFutureSkill_ShouldThrowExceptionWhenDelegateTriesToApproveWithoutPermission() {
        CustomUserPrincipal delegatePrincipal = new CustomUserPrincipal(
                "Delegate",
                "User",
                "delegateuser@epam.com",
                RoleConstants.ROLE_PRACTICE,
                true
        );

        FutureSkillRequestListDTO requestDTO = FutureSkillRequestListDTO.builder()
                .submissionStatus(SubmissionStatus.A)
                .futureSkills(Collections.emptyList())
                .build();
        when(practiceDelegateUserService.isApprovalRequired()).thenReturn(true);
        when(practiceDelegateUserService.hasAccessToFeature(PracticeDelegationFeaturesConstants.FUTURE_SKILL_FEATURE))
                .thenReturn(true);
        FutureSkillException exception = assertThrows(FutureSkillException.class, () ->
                futureSkillService.saveFutureSkill(delegatePrincipal, requestDTO)
        );
        assertEquals(ErrorMessages.PRACTICE_FORM_NO_APPROVAL_PERMISSION, exception.getMessage());
        verifyNoInteractions(futureSkillRepository);
    }

    @Test
    void testSaveFutureSkill_ShouldThrowExceptionWhenDelegateHasNoAccessToFeature() {
        CustomUserPrincipal delegatePrincipal = new CustomUserPrincipal(
                "Delegate",
                "User",
                "delegateuser@epam.com",
                RoleConstants.ROLE_PRACTICE,
                true
        );

        FutureSkillRequestListDTO requestDTO = FutureSkillRequestListDTO.builder()
                .submissionStatus(SubmissionStatus.D)
                .futureSkills(Collections.emptyList())
                .build();

        when(practiceDelegateUserService.hasAccessToFeature(PracticeDelegationFeaturesConstants.FUTURE_SKILL_FEATURE))
                .thenReturn(false);

        FutureSkillException exception = assertThrows(FutureSkillException.class, () ->
                futureSkillService.saveFutureSkill(delegatePrincipal, requestDTO)
        );

        assertEquals(ErrorMessages.PRACTICE_FORM_NO_APPROVAL_PERMISSION, exception.getMessage());

        verifyNoInteractions(futureSkillRepository);
    }

    @Test
    void testSaveFutureSkill_WhenNewSkillsSaved() {
        UserDTO userDTO = getUserDTO();
        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        FutureSkillRequestListDTO requestListDTO = FutureSkillRequestListDTO.builder()
                .submissionStatus(SubmissionStatus.S)
                .futureSkills(List.of(
                        FutureSkillRequestDTO.builder()
                                .categoryName("Category1")
                                .answer("Answer1")
                                .build()
                ))
                .build();

        FutureSkillCategory category = FutureSkillCategory.builder().id(1L).categoryName("Category1").build();

        when(userService.getUser(userPrincipal.getEmail())).thenReturn(userDTO);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(futureSkillCategoryRepository.findByCategoryName("Category1")).thenReturn(Optional.of(category));
        when(identificationClosureService.isIdentificationClosureDataPresent()).thenReturn(false);

        String response = futureSkillService.saveFutureSkill(userPrincipal, requestListDTO);

        verify(futureSkillRepository, times(2)).save(any(FutureSkill.class));
        assertEquals(Constants.FUTURE_SKILL_STATUS_S, response);
    }

    @Test
    void testSaveFutureSkill_WhenCategoryNotFound() {
        UserDTO userDTO = getUserDTO();
        FutureSkillRequestListDTO requestListDTO = FutureSkillRequestListDTO.builder()
                .submissionStatus(SubmissionStatus.S)
                .futureSkills(List.of(
                        FutureSkillRequestDTO.builder()
                                .categoryName("InvalidCategory")
                                .answer("Answer1")
                                .build()
                ))
                .build();

        when(userService.getUser(userPrincipal.getEmail())).thenReturn(userDTO);
        when(futureSkillCategoryRepository.findByCategoryName("InvalidCategory"))
                .thenReturn(Optional.empty());

        FutureSkillException exception = assertThrows(FutureSkillException.class,
                () -> futureSkillService.saveFutureSkill(userPrincipal, requestListDTO));

        assertEquals(ErrorMessages.FUTURE_SKILLS_CATEGORY_NOT_FOUND + "InvalidCategory", exception.getMessage());
    }

    @Test
    void testGetPracticeDetailsAndSubmissionStatus_Success() {
        List<UserResponseDTO> practiceHeadList = List.of(
                UserResponseDTO.builder()
                        .practice("Big Data")
                        .firstName("Vriddhi")
                        .lastName("Darak")
                        .status("Active")
                        .message("Practice Head for Big Data")
                        .build(),
                UserResponseDTO.builder()
                        .practice("Testing")
                        .firstName("Shaik")
                        .lastName("Basha")
                        .status("Active")
                        .message("Practice Head for Testing")
                        .build(),
                UserResponseDTO.builder()
                        .practice("Python")
                        .firstName("Yathin")
                        .lastName("Singh")
                        .status("Active")
                        .message("Practice Head for Python")
                        .build()
        );
        when(userService.getUsersByRole(RoleConstants.PRACTICE, Optional.of(Constants.USER_STATUS_ACTIVE)))
                .thenReturn(practiceHeadList);

        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        latestVersion.setVersionName("2025 Q1");
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);

        when(futureSkillRepository.existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                 "Big Data", latestVersion, false
        )).thenReturn(true);

        when(futureSkillRepository.findByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                "Big Data", latestVersion, false
        )).thenReturn(List.of(
                FutureSkill.builder()
                        .submissionStatus(SubmissionStatus.S)
                        .practiceName("Big Data")
                        .answer("Java, Big Data")
                        .futureSkillCategory(mockFutureSkillCategory())
                        .isForAspirationRating(false)
                        .topTalentExcelVersion(latestVersion)
                        .lastUpdated(LocalDateTime.of(2025, 4, 23, 10, 0))
                        .lastUpdatedBy("Vriddhi Darak")
                        .build()
        ));

        when(futureSkillRepository.existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                 "Testing", latestVersion, false
        )).thenReturn(false);

        when(futureSkillRepository.existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                "Python", latestVersion, false
        )).thenReturn(false);

        when(futureSkillCategoryRepository.findByCategoryName("Future Skills (3–5 years)"))
                .thenReturn(Optional.of(mockFutureSkillCategory()));

        List<FutureSkillPracticeDTO> result = futureSkillService.getPracticeDetailsAndSubmissionStatus();

        assertNotNull(result);
        assertEquals(3, result.size());

        FutureSkillPracticeDTO vriddhi = result.get(0);
        assertEquals("Big Data", vriddhi.getPracticeName());
        assertEquals("Vriddhi Darak", vriddhi.getPracticeHeadName());
        assertEquals("S", vriddhi.getSubmissionStatus());
        assertEquals("Java, Big Data", vriddhi.getSkills());
        assertEquals("2025-04-23", vriddhi.getDate());
        assertEquals("Vriddhi Darak", vriddhi.getSubmittedBy());

        FutureSkillPracticeDTO shaik = result.get(1);
        assertEquals("Testing", shaik.getPracticeName());
        assertEquals("Shaik Basha", shaik.getPracticeHeadName());
        assertEquals("NA", shaik.getSubmissionStatus());
        assertEquals("NA", shaik.getSkills());
        assertEquals("NA", shaik.getDate());
        assertEquals("NA", shaik.getSubmittedBy());

        FutureSkillPracticeDTO yathin = result.get(2);
        assertEquals("Python", yathin.getPracticeName());
        assertEquals("Yathin Singh", yathin.getPracticeHeadName());
        assertEquals("NA", yathin.getSubmissionStatus());
        assertEquals("NA", yathin.getSkills());
        assertEquals("NA", yathin.getDate());
        assertEquals("NA", yathin.getSubmittedBy());
    }

    private FutureSkillCategory mockFutureSkillCategory() {
        FutureSkillCategory category = new FutureSkillCategory();
        category.setId(1L);
        category.setCategoryName("Future Skills (3–5 years)");
        return category;
    }

    @Test
    void testNotifyIfIdentificationPhaseEnded_WhenActiveUsers() {
        UserDTO activeUser = UserDTO.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .isActive(Constants.USER_STATUS_ACTIVE)
                .build();

        when(userService.getUsersWithPracticeHeadRole()).thenReturn(List.of(activeUser));

        futureSkillService.notifyIfIdentificationPhaseEnded();

        verify(mailGenerationService, times(1)).generatePracticeRemainderMailAndSend(
                "john.doe@example.com", Constants.FUTURE_SKILL_EMAIL_SUBJECT, Constants.FUTURE_SKILL_EMAIL_FILENAME, "John Doe");
    }

    @Test
    void testNotifyIfIdentificationPhaseEnded_WhenNoActiveUsers() {
        UserDTO inactiveUser = UserDTO.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .isActive(Constants.USER_STATUS_INACTIVE)
                .build();

        when(userService.getUsersWithPracticeHeadRole()).thenReturn(List.of(inactiveUser));

        futureSkillService.notifyIfIdentificationPhaseEnded();

        verify(mailGenerationService, never()).generatePracticeRemainderMailAndSend(anyString(), anyString(), anyString(), anyString());
    }


    private UserDTO getUserDTO() {
        return UserDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .practice("IT")
                .build();
    }

    @Test
    public void testSaveFutureSkill_TriggerEmailNotification_WhenSubmissionStatusIsA_AndIsDelegate() {
        CustomUserPrincipal userPrincipal = mock(CustomUserPrincipal.class);
        when(userPrincipal.isDelegate()).thenReturn(true);

        UserDTO userDTO = new UserDTO();
        userDTO.setPractice("Software Development");
        when(userService.getUser(userPrincipal.getEmail())).thenReturn(userDTO);

        FutureSkillRequestListDTO mockedRequestListDTO = mock(FutureSkillRequestListDTO.class);
        when(mockedRequestListDTO.getSubmissionStatus()).thenReturn(SubmissionStatus.A);

        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);

        when(practiceDelegateUserService.hasAccessToFeature(PracticeDelegationFeaturesConstants.FUTURE_SKILL_FEATURE))
                .thenReturn(true);
        futureSkillService.saveFutureSkill(userPrincipal, mockedRequestListDTO);
        verify(emailService).sendMailToAdmin(userPrincipal, "Software Development");
        verifyNoMoreInteractions(emailService);
    }

    @Test
    public void testSaveFutureSkill_TriggerEmailNotification_WhenSubmissionStatusIsS_AndNotDelegate() {
        CustomUserPrincipal userPrincipal = mock(CustomUserPrincipal.class);
        when(userPrincipal.isDelegate()).thenReturn(false);

        UserDTO userDTO = new UserDTO();
        userDTO.setPractice("Software Development");
        when(userService.getUser(userPrincipal.getEmail())).thenReturn(userDTO);

        FutureSkillRequestListDTO mockedRequestListDTO = mock(FutureSkillRequestListDTO.class);
        when(mockedRequestListDTO.getSubmissionStatus()).thenReturn(SubmissionStatus.S);

        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        futureSkillService.saveFutureSkill(userPrincipal, mockedRequestListDTO);
        verify(emailService).sendMailToAdmin(userPrincipal, "Software Development");
        verifyNoMoreInteractions(emailService);
    }

    @Test
    public void testSaveFutureSkill_EmailNotification_WhenSubmissionStatusIsA_AndNotDelegate() {
        CustomUserPrincipal userPrincipal = mock(CustomUserPrincipal.class);
        when(userPrincipal.isDelegate()).thenReturn(false);

        UserDTO userDTO = new UserDTO();
        userDTO.setPractice("Software Development");
        when(userService.getUser(userPrincipal.getEmail())).thenReturn(userDTO);

        FutureSkillRequestListDTO mockedRequestListDTO = mock(FutureSkillRequestListDTO.class);
        when(mockedRequestListDTO.getSubmissionStatus()).thenReturn(SubmissionStatus.A);
        futureSkillService.saveFutureSkill(userPrincipal, mockedRequestListDTO);
        verify(emailService).sendMailToAdmin(userPrincipal, "Software Development");
    }

    @Test
    public void testSaveFutureSkill_NoEmailNotification_WhenSubmissionStatusIsNotA() {
        UserDTO userDTO = new UserDTO();
        userDTO.setPractice("Software Development");
        Mockito.when(userService.getUser(userPrincipal.getEmail())).thenReturn(userDTO);

        FutureSkillRequestListDTO mockedRequestListDTO = mock(FutureSkillRequestListDTO.class);
        when(mockedRequestListDTO.getSubmissionStatus()).thenReturn(SubmissionStatus.NA);

        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);

        futureSkillService.saveFutureSkill(userPrincipal, mockedRequestListDTO);
        verify(emailService, never()).sendMailToAdmin(any(CustomUserPrincipal.class), anyString());
    }

}