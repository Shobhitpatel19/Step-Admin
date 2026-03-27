package com.top.talent.management.service;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.PracticeDelegationDTO;
import com.top.talent.management.dto.PracticeDelegationFeatureDTO;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.entity.Delegation;
import com.top.talent.management.entity.PracticeDelegationFeature;
import com.top.talent.management.entity.Role;
import com.top.talent.management.exception.PracticeDelegationException;
import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.mapper.PracticeDelegationMapper;
import com.top.talent.management.repository.PracticeDelegationFeatureRepository;
import com.top.talent.management.repository.PracticeDelegationRepository;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.DelegationServiceImpl;
import com.top.talent.management.service.impl.PracticeDelegateEmailServiceImpl;
import com.top.talent.management.service.impl.UserProfileServiceImpl;
import com.top.talent.management.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DelegationServiceImplTest {
    @Mock
    private PracticeDelegationRepository practiceDelegationRepository;
    @Mock
    private PracticeDelegationFeatureRepository practiceDelegationFeatureRepository;
    @Mock
    private PracticeDelegationMapper practiceDelegationMapper;
    @Mock
    private UserService userService;
    @Mock
    private PracticeDelegateEmailServiceImpl practiceDelegateEmailServiceImpl;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserProfileServiceImpl userProfileService;

    @InjectMocks
    private DelegationServiceImpl practiceDelegationService;

    private final CustomUserPrincipal loggedInUser;

    private UserProfile epamApiEmployee;
    private Delegation delegationTo;
    private PracticeDelegationDTO practiceDelegationToDTO;
    private List<PracticeDelegationFeature> practiceDelegationFeatures;

    public DelegationServiceImplTest() {
        Authentication authentication = TestUtils.getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);
        this.loggedInUser = (CustomUserPrincipal) authentication.getPrincipal();
    }

    @BeforeEach
    public void setup() {
        epamApiEmployee = UserProfile.builder()
                .fullName("Delegate User")
                .firstName("Delegate")
                .lastName("User")
                .uid(120120L)
                .jobDesignation("Director Enterprise Architecture")
                .email("delegate_user@epam.com")
                .jobTrack("B")
                .jobTrackLevel("4")
                .primarySkill("Enterprise Architecture")
                .unit("Enterprise Architecture")
                .profileType("Employee")
                .photo("profilePicture.jpg")
                .build();

        delegationTo = Delegation.builder()
                .delegatedBy(loggedInUser.getEmail())
                .delegatedTo("delegate_user@epam.com")
                .approvalRequired(true)
                .practiceDelegationFeatures(
                        List.of(
                                PracticeDelegationFeature.builder().name("Feature1").build(),
                                PracticeDelegationFeature.builder().name("Feature2").build()
                        )
                )
                .build();

        practiceDelegationToDTO = PracticeDelegationDTO.builder()
                .delegatedBy(loggedInUser.getEmail())
                .delegatedTo(epamApiEmployee)
                .approvalRequired(true)
                .practiceDelegationFeatures(
                        List.of(
                                PracticeDelegationFeatureDTO.builder().name("Feature1").build(),
                                PracticeDelegationFeatureDTO.builder().name("Feature2").build()
                        )
                )
                .build();

        practiceDelegationFeatures = List.of(
                PracticeDelegationFeature.builder().name("Feature1").build(),
                PracticeDelegationFeature.builder().name("Feature2").build()
        );

        lenient().when(userService.getUser(loggedInUser.getEmail()))
                .thenReturn(UserDTO.builder().practice("Java").build());

        lenient().when(userProfileService.fetchUserByEmail(anyString()))
                .thenReturn(epamApiEmployee);

        lenient().when(practiceDelegationFeatureRepository.findByNameIn(anyList()))
                .thenReturn(practiceDelegationFeatures);

        lenient().when(practiceDelegationRepository.save(any()))
                .thenReturn(delegationTo);

        lenient().when(practiceDelegationMapper.toPracticeDelegationDto(any()))
                .thenReturn(practiceDelegationToDTO);

        lenient().doNothing().when(practiceDelegateEmailServiceImpl).sendNotificationMailToDelegate(
                any(),anyString(), anyString(),eq("create"));
    }

    @Test
     void testCreatePracticeDelegate_T2() {

        Delegation existingDelegate = Delegation.builder()
                .delegatedBy(loggedInUser.getEmail())
                .delegatedTo("delegate_user@epam.com")
                .approvalRequired(true)
                .practiceDelegationFeatures(
                        List.of(
                                PracticeDelegationFeature.builder().name("Feature1").build(),
                                PracticeDelegationFeature.builder().name("Feature2").build()
                        )
                )
                .build();
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());
        when(practiceDelegationRepository.findByDelegatedBy(anyString()))
                .thenReturn(existingDelegate);

        when(userService.getUser(delegationTo.getDelegatedTo())).thenThrow(UserNotFoundException.class);
        PracticeDelegationDTO savedDelegate = practiceDelegationService.createPracticeDelegate(
                delegationTo, "");


        assertEquals("delegate_user@epam.com", savedDelegate.getDelegatedTo().getEmail());
        assertEquals(loggedInUser.getEmail(), savedDelegate.getDelegatedBy());
        assertEquals(2, savedDelegate.getPracticeDelegationFeatures().size());
    }

    @Test
   void testCreatePracticeDelegate_T3() {
        Delegation existingDelegate = Delegation.builder()
                .delegatedBy(loggedInUser.getEmail())
                .delegatedTo("delegate_user_1@epam.com")
                .build();

        when(practiceDelegationRepository.findByDelegatedBy(anyString()))
                .thenReturn(existingDelegate);
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());

        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.createPracticeDelegate(delegationTo, ""));
        assertEquals(ErrorMessages.PRACTICE_DELEGATION_ALREADY_DELEGATED, practiceDelegationException.getMessage());
    }

    @Test
    void testCreatePracticeDelegate_WithCompetency() {
        UserDTO practiceHead = UserDTO.builder().email("test_user@epam.com").build();
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("Java").roleName("P").build());
        when(practiceDelegationRepository.findByDelegatedBy(practiceHead.getEmail())).thenReturn(null);
        when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(Role.builder().id(1L)
                .name(RoleConstants.PRACTICE).build());
        when(userService.getUser(delegationTo.getDelegatedTo())).thenThrow(UserNotFoundException.class);

        PracticeDelegationDTO savedDelegate = practiceDelegationService.createPracticeDelegate(
                delegationTo, "");

        assertEquals("delegate_user@epam.com", savedDelegate.getDelegatedTo().getEmail());

        assertEquals(2, savedDelegate.getPracticeDelegationFeatures().size());
    }

    @Test
    void testCreatePracticeDelegate_WithDiffCompetency() {
        String competency = "Java";
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());

        PracticeDelegationException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.createPracticeDelegate(delegationTo, competency));

        assertEquals(ErrorMessages.NOT_SEND_COMPETENCY_PH, practiceDelegationException.getMessage());
    }


    @Test
   void testCreatePracticeDelegate_T4() {

        when(userService.getUser("delegate_user@epam.com"))
                .thenReturn(UserDTO.builder().practice("SomeOtherPractice").build());

        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("Java").roleName("P").build());

        PracticeDelegationException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.createPracticeDelegate(delegationTo, ""));

        assertEquals(ErrorMessages.PRACTICE_DELEGATION_IS_STEP_USER, practiceDelegationException.getMessage());
    }

    @Test
    void testCreatePracticeDelegate_T5() {

        when(practiceDelegationRepository.findByDelegatedBy(delegationTo.getDelegatedBy()))
                .thenReturn(null);

        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());


        when(userService.getUser(delegationTo.getDelegatedTo())).thenThrow(UserNotFoundException.class);


        when(userProfileService.fetchUserByEmail(delegationTo.getDelegatedTo()))
                .thenReturn(null);

        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.createPracticeDelegate(delegationTo, ""));
        assertEquals(ErrorMessages.PRACTICE_DELEGATION_USER_NOT_FOUND, practiceDelegationException.getMessage());
    }

    @Test
    void testCreatePracticeDelegate_T6() {

        epamApiEmployee.setJobTrack("B");
        epamApiEmployee.setJobTrackLevel("2");
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());

        when(practiceDelegationRepository.findByDelegatedBy(delegationTo.getDelegatedBy()))
                .thenReturn(null);
        when(userService.getUser(delegationTo.getDelegatedTo())).thenThrow(UserNotFoundException.class);


        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.createPracticeDelegate(delegationTo, ""));
        assertEquals(ErrorMessages.PRACTICE_DELEGATION_USER_NOT_ELIGIBLE, practiceDelegationException.getMessage());
    }

    @Test
   void testCreatePracticeDelegate_T7() {
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());

        when(practiceDelegationRepository.findByDelegatedBy(delegationTo.getDelegatedBy()))
                .thenReturn(null);

        when(userService.getUser(delegationTo.getDelegatedTo())).thenThrow(UserNotFoundException.class);


        delegationTo.setPracticeDelegationFeatures(null);

        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.createPracticeDelegate(delegationTo, ""));
        assertEquals(ErrorMessages.PRACTICE_DELEGATION_NO_FEATURE_SELECTED,
                practiceDelegationException.getMessage());
    }


    @Test
   void testCreatePracticeDelegate_T8() {
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());

        when(practiceDelegationRepository.findByDelegatedBy(delegationTo.getDelegatedBy()))
                .thenReturn(null);

        when(userService.getUser(delegationTo.getDelegatedTo())).thenThrow(UserNotFoundException.class);


        delegationTo.setPracticeDelegationFeatures(List.of());

        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.createPracticeDelegate(delegationTo,""));

        assertEquals(ErrorMessages.PRACTICE_DELEGATION_NO_FEATURE_SELECTED,
                practiceDelegationException.getMessage());
    }



    @Test
    void testCreatePracticeDelegate_T9() {
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());

        when(practiceDelegationRepository.findByDelegatedBy(delegationTo.getDelegatedBy()))
                .thenReturn(null);

        when(userService.getUser(delegationTo.getDelegatedTo())).thenThrow(UserNotFoundException.class);


        delegationTo.setApprovalRequired(null);

        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.createPracticeDelegate(delegationTo,""));

        assertEquals(ErrorMessages.PRACTICE_DELEGATION_NO_ACCESS_LEVEL_SELECTED,
                practiceDelegationException.getMessage());
    }


    @Test
   void testCreatePracticeDelegate_T10() {
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());

        when(practiceDelegationRepository.findByDelegatedBy(delegationTo.getDelegatedBy()))
                .thenReturn(null);

        when(userService.getUser(delegationTo.getDelegatedTo())).thenThrow(UserNotFoundException.class);


        List<PracticeDelegationFeature> invalidFeatureList = new ArrayList<>(
                delegationTo.getPracticeDelegationFeatures());
        invalidFeatureList.add(PracticeDelegationFeature.builder().name("Feature3").build());

        delegationTo.setPracticeDelegationFeatures(invalidFeatureList);

        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.createPracticeDelegate(delegationTo,""));

        assertEquals(ErrorMessages.PRACTICE_DELEGATION_INVALID_FEATURE_SELECTED,
                practiceDelegationException.getMessage());
    }


    @Test
     void testGetPracticeDelegate_T1() {
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());

        when(practiceDelegationRepository.findByDelegatedBy(loggedInUser.getEmail()))
                .thenReturn(delegationTo);

        PracticeDelegationDTO practiceDelegationDTO = practiceDelegationService.getPracticeDelegate("");

        assertEquals(practiceDelegationToDTO.getDelegatedBy(),
                practiceDelegationDTO.getDelegatedBy());
        assertEquals(practiceDelegationToDTO.getDelegatedTo(),
                practiceDelegationDTO.getDelegatedTo());
        assertEquals(practiceDelegationToDTO.getApprovalRequired(),
                practiceDelegationDTO.getApprovalRequired());
        assertEquals(practiceDelegationToDTO.getPracticeDelegationFeatures(),
                practiceDelegationDTO.getPracticeDelegationFeatures());
    }

    @Test
     void testGetPracticeDelegate_T2() {
        when(practiceDelegationRepository.findByDelegatedBy(loggedInUser.getEmail()))
                .thenReturn(null);
        when(userService.getUser(loggedInUser.getEmail())).thenReturn(UserDTO.builder().practice("").roleName("P").build());

        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.getPracticeDelegate(""));
        assertEquals(ErrorMessages.PRACTICE_DELEGATION_NEVER_DELEGATED,
                practiceDelegationException.getMessage());
    }




    @Test
     void testGetPracticeDelegateByDelegatedTo_T1() {
        when(practiceDelegationRepository.findByDelegatedTo(loggedInUser.getEmail()))
                .thenReturn(delegationTo);

        PracticeDelegationDTO practiceDelegationDTO = practiceDelegationService.getPracticeDelegateByDelegatedTo();

        assertEquals(practiceDelegationToDTO.getDelegatedBy(),
                practiceDelegationDTO.getDelegatedBy());
        assertEquals(practiceDelegationToDTO.getDelegatedTo(),
                practiceDelegationDTO.getDelegatedTo());
        assertEquals(practiceDelegationToDTO.getApprovalRequired(),
                practiceDelegationDTO.getApprovalRequired());
        assertEquals(practiceDelegationToDTO.getPracticeDelegationFeatures(),
                practiceDelegationDTO.getPracticeDelegationFeatures());
    }



    @Test
     void testGetPracticeDelegateByDelegatedTo_T2() {
        when(practiceDelegationRepository.findByDelegatedTo(loggedInUser.getEmail()))
                .thenReturn(null);

        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.getPracticeDelegateByDelegatedTo());
        assertEquals(ErrorMessages.PRACTICE_DELEGATION_DELEGATE_NOT_FOUND,
                practiceDelegationException.getMessage());
    }


    @Test
     void testDeletePracticeDelegate_T1() {
        when(practiceDelegationRepository.findByDelegatedBy(loggedInUser.getEmail()))
                .thenReturn(delegationTo);

        when(userService.removeUser(delegationTo.getDelegatedTo()))
                .thenReturn(UserDTO.builder().firstName("Delegate").lastName("User").build());

        doNothing().when(practiceDelegationRepository).delete(delegationTo);

        delegationTo.setPracticeDelegationFeatures(
                new ArrayList<>(practiceDelegationFeatures)
        );

        PracticeDelegationDTO practiceDelegationDTO = practiceDelegationService.deletePracticeDelegate("");

        assertEquals(practiceDelegationToDTO.getDelegatedBy(),
                practiceDelegationDTO.getDelegatedBy());
        assertEquals(practiceDelegationToDTO.getDelegatedTo(),
                practiceDelegationDTO.getDelegatedTo());
        assertEquals(practiceDelegationToDTO.getApprovalRequired(),
                practiceDelegationDTO.getApprovalRequired());
        assertEquals(practiceDelegationToDTO.getPracticeDelegationFeatures(),
                practiceDelegationDTO.getPracticeDelegationFeatures());
    }

    @Test
     void testDeletePracticeDelegate_T2() {
        when(practiceDelegationRepository.findByDelegatedBy(loggedInUser.getEmail()))
                .thenReturn(null);

        RuntimeException practiceDelegationException = assertThrows(PracticeDelegationException.class,
                () -> practiceDelegationService.deletePracticeDelegate(""));
        assertEquals(ErrorMessages.PRACTICE_DELEGATION_NEVER_DELEGATED,
                practiceDelegationException.getMessage());
    }

}