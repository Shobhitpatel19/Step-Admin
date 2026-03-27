package com.top.talent.management.service;

import com.top.talent.management.constants.AspirationPriority;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.AspirationApprovalRequestDTO;
import com.top.talent.management.dto.AspirationDTO;
import com.top.talent.management.dto.AspirationItemDTO;
import com.top.talent.management.dto.AspirationPriorityDTO;
import com.top.talent.management.dto.AspirationResponseDTO;
import com.top.talent.management.dto.SubmitAspirationsRequest;
import com.top.talent.management.entity.AspirationDetail;
import com.top.talent.management.entity.CandidateAspiration;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.CandidateAspirationException;
import com.top.talent.management.mapper.CandidateAspirationMapper;
import com.top.talent.management.repository.AspirationDetailRespository;
import com.top.talent.management.repository.CandidateAspirationRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.CandidateAspirationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.top.talent.management.utils.TestUtils.getMockAuthentication;
import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CandidateAspirationServiceTest {

    @InjectMocks
    private CandidateAspirationServiceImpl candidateAspirationService;

    @Mock
    private UserService userService;

    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;

    @Mock
    private CandidateAspirationRepository candidateAspirationRepository;

    @Mock
    AspirationDetailRespository aspirationDetailRespository;

    @InjectMocks
    private CandidateAspirationServiceImpl aspirationService;

    @Mock
    private CandidateAspirationMapper mapper;

    @Test
    public void testSaveAspiration_Success() {
        String email = "test_user@epam.com";
        Long uid = 1L;

        Authentication authentication= getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        AspirationDTO aspirationDTO = mock(AspirationDTO.class);
        List<AspirationItemDTO> aspirationList = new ArrayList<>();
        AspirationItemDTO itemDTO =AspirationItemDTO.builder()
                .title("Test Title")
                .description("Test Description")
                .build();
        itemDTO.setInputValue("Test Value");
        aspirationList.add(itemDTO);
        when(aspirationDTO.getAspirationList()).thenReturn(aspirationList);

        CandidateAspiration candidateAspiration = mock(CandidateAspiration.class);
        when(candidateAspirationRepository.saveAll(anyList())).thenReturn(Collections.singletonList(candidateAspiration));

        when(userService.getUuidByEmail(email)).thenReturn(uid);
        when(aspirationDTO.isFutureSkillAcknowledged()).thenReturn(true);

         AspirationDTO result = candidateAspirationService.saveAspiration(principal, aspirationDTO);
        assertNotNull(result, "The result should not be null");

        verify(userService).getUuidByEmail(email);
        verify(candidateAspirationRepository).saveAll(anyList());
    }



    @Test
    public void testGetAspirationByPriority_NotFound() {
        String email = "test_user@epam.com";
        String priority = "ASPIRATION1";
        Long uid = 1L;

        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);
        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(candidateAspirationRepository.findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.ASPIRATION1, latestVersion))
                .thenReturn(Collections.emptyList());
        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.getAspirationByPriority(principal, AspirationPriority.ASPIRATION1);
        });

        assertEquals(ErrorMessages.ASPIRATION_NOT_FOUND + priority, exception.getMessage(), "The exception message should match the aspiration not found error");
        verify(userService).getUuidByEmail(email);
        verify(candidateAspirationRepository).findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.ASPIRATION1, latestVersion);
    }

    @Test
    public void testGetAspirationByPriority_NA() {
        String priority = "NA";

        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        AspirationDetail aspirationDetail = mock(AspirationDetail.class);

        when(aspirationDetailRespository.findAll()).thenReturn(Collections.singletonList(aspirationDetail));

        AspirationPriorityDTO result = candidateAspirationService.getAspirationByPriority(principal, AspirationPriority.valueOf(priority));
        assertNotNull(result, "The result should not be null");

    }

    @Test
    public void testGetAspirationByPriority_Success() {
        String email = "test_user@epam.com";
        String priority = "ASPIRATION1";
        Long uid = 1L;

        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);
        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        CandidateAspiration aspiration = mock(CandidateAspiration.class);

        List<CandidateAspiration> candidateAspirations = Collections.singletonList(aspiration);
        when(candidateAspirationRepository.findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.ASPIRATION1, latestVersion))
                .thenReturn(candidateAspirations);
        AspirationPriorityDTO result = candidateAspirationService.getAspirationByPriority(principal, AspirationPriority.valueOf(priority));

        assertNotNull(result, "The result should not be null");
        assertEquals(AspirationPriority.valueOf(priority), result.getPriority(), "Priority should match the requested priority");
        verify(userService).getUuidByEmail(email);
        verify(candidateAspirationRepository).findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.ASPIRATION1, latestVersion);
    }

    @Test
    public void testDeleteAspiration_Success() {
        String email = "user@epam.com";
        String priority = "ASPIRATION1";
        Long uid = 1L;

        CustomUserPrincipal principal = mock(CustomUserPrincipal.class);
        when(principal.getEmail()).thenReturn(email);

        CandidateAspiration aspiration = new CandidateAspiration();
        aspiration.setPriority(AspirationPriority.ASPIRATION1);
        aspiration.setSubmissionStatus(SubmissionStatus.D);

        List<CandidateAspiration> aspirationsToDelete = new ArrayList<>();
        aspirationsToDelete.add(aspiration);

        when(userService.getUuidByEmail(email)).thenReturn(uid);
        when(candidateAspirationRepository.findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.ASPIRATION1, null))
                .thenReturn(aspirationsToDelete);

        AspirationDTO result = candidateAspirationService.deleteAspiration(principal,AspirationPriority.valueOf( priority));

        assertNotNull(result, "The result should not be null");
        verify(userService).getUuidByEmail(email);
        verify(candidateAspirationRepository).findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.ASPIRATION1, null);
    }



    @Test
    public void testDeleteAspiration_NotFound() {
        String email = "user@epam.com";
        String priority = "ASPIRATION1";
        Long uid = 1L;

        CustomUserPrincipal principal = mock(CustomUserPrincipal.class);
        when(principal.getEmail()).thenReturn(email);

        when(userService.getUuidByEmail(email)).thenReturn(uid);
        when(candidateAspirationRepository.findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.valueOf(priority), null))
                .thenReturn(Collections.emptyList());

        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.deleteAspiration(principal, AspirationPriority.valueOf(priority));
        });

        assertEquals(ErrorMessages.ASPIRATION_NOT_FOUND+"ASPIRATION1", exception.getMessage(), "The exception message should match the aspiration not found error");
        verify(userService).getUuidByEmail(email);
        verify(candidateAspirationRepository).findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.valueOf(priority), null);
    }


    @Test
    public void testEditAspiration_AspirationNotFound() {
        String email = "test_user@epam.com";
        String priority = "ASPIRATION1";
        Long uid = 1L;

        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);

        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(candidateAspirationRepository.findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.valueOf(priority), latestVersion))
                .thenReturn(Collections.emptyList());

        AspirationDTO aspirationDTO = mock(AspirationDTO.class);

        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.editAspiration(principal, AspirationPriority.valueOf(priority), aspirationDTO);
        });
        assertEquals(ErrorMessages.ASPIRATION_NOT_FOUND, exception.getMessage(), "The exception message should match the aspiration not found error");
        verify(userService).getUuidByEmail(email);
        verify(candidateAspirationRepository).findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.valueOf(priority), latestVersion);
    }

    @Test
    public void testSubmitAspirations_NoPriority() {
        Long uid = 1L;
        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        SubmitAspirationsRequest submitRequest = new SubmitAspirationsRequest(true);

        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        List<CandidateAspiration> draftAspirations = Collections.singletonList(mock(CandidateAspiration.class));

        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, latestVersion)).thenReturn(draftAspirations);
        when(draftAspirations.get(0).getPriority()).thenReturn(null);

        assertThrows(RuntimeException.class, () -> candidateAspirationService.submitAspirations(principal,submitRequest));
    }

    @Test
    void testSubmitAspirations_ThrowsException_WhenAcknowledgmentIsFalse() {
        CustomUserPrincipal principal = mock(CustomUserPrincipal.class);
        when(principal.getEmail()).thenReturn("test@example.com");

        SubmitAspirationsRequest submitRequest = mock(SubmitAspirationsRequest.class);
        when(submitRequest.isSubmitAcknowledged()).thenReturn(false);
        CandidateAspirationException exception = assertThrows(
                CandidateAspirationException.class,
                () -> candidateAspirationService.submitAspirations(principal, submitRequest)
        );
        assertEquals(ErrorMessages.ACKNOWLEDGMENT_BEFORE_SUBMIT, exception.getMessage());
    }

    @Test
    void testSaveAspiration_shouldThrowExceptionWhenAcknowledgmentIsFalse() {
        String email = "test_user@epam.com";
        CustomUserPrincipal principal = mock(CustomUserPrincipal.class);
        when(principal.getEmail()).thenReturn(email);
        AspirationDTO aspirationDTO = mock(AspirationDTO.class);
        when(aspirationDTO.isAspiration1()).thenReturn(true);
        when(aspirationDTO.isFutureSkillAcknowledged()).thenReturn(false);
        CandidateAspirationException exception = assertThrows(
                CandidateAspirationException.class,
                () -> candidateAspirationService.saveAspiration(principal, aspirationDTO)
        );
        assertEquals(ErrorMessages.ACKNOWLEDGMENT_BEFORE_CREATE_ASPIRATION, exception.getMessage());
    }

    @Test
    public void testSaveAspiration_ExceedsLimit() {
        String email = "test_user@epam.com";
        Long uid = 1L;

        Authentication authentication= getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);
        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        List<CandidateAspiration> existingAspirations = Arrays.asList(
                CandidateAspiration.builder().priority(AspirationPriority.ASPIRATION1).submissionStatus(SubmissionStatus.D).build(),
                CandidateAspiration.builder().priority(AspirationPriority.ASPIRATION2).submissionStatus(SubmissionStatus.D).build()
        );
        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, latestVersion)).thenReturn(existingAspirations);
        AspirationDTO aspirationDTO = mock(AspirationDTO.class);
        when(aspirationDTO.isAspiration1()).thenReturn(true);
        when(aspirationDTO.isFutureSkillAcknowledged()).thenReturn(true);

        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.saveAspiration(principal, aspirationDTO);
        });
        assertEquals(ErrorMessages.ASPIRATION_LIMIT_EXCEEDED, exception.getMessage());
    }

    @Test
    public void testSaveAspiration_AlreadySubmitted() {
        String email = "test_user@epam.com";
        Long uid = 1L;

        Authentication authentication= getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);
        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        List<CandidateAspiration> existingAspirations = Arrays.asList(
                CandidateAspiration.builder().priority(AspirationPriority.ASPIRATION1).submissionStatus(SubmissionStatus.S).build(),
                CandidateAspiration.builder().priority(AspirationPriority.ASPIRATION2).submissionStatus(SubmissionStatus.S).build()
        );
        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, latestVersion)).thenReturn(existingAspirations);
        AspirationDTO aspirationDTO = mock(AspirationDTO.class);
        when(aspirationDTO.isAspiration1()).thenReturn(true);
        when(aspirationDTO.isFutureSkillAcknowledged()).thenReturn(true);

        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.saveAspiration(principal, aspirationDTO);
        });
        assertEquals(ErrorMessages.ASPIRATIONS_SUBMITTED, exception.getMessage());
    }

    @Test
    public void testSaveAspiration_CreateSecondaryWhenPrimaryExists() {
        String email = "test_user@epam.com";
        Long uid = 1L;
        Authentication authentication= getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);
        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        List<CandidateAspiration> existingAspirations = Collections.singletonList(
                CandidateAspiration.builder().priority(AspirationPriority.ASPIRATION1)
                        .submissionStatus(SubmissionStatus.D).build()
        );
        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, latestVersion)).thenReturn(existingAspirations);
        AspirationDTO aspirationDTO = mock(AspirationDTO.class);
        when(aspirationDTO.isAspiration1()).thenReturn(true);
        when(aspirationDTO.isFutureSkillAcknowledged()).thenReturn(true);
        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.saveAspiration(principal, aspirationDTO);
        });
        assertEquals(ErrorMessages.CREATE_ASPIRATION2, exception.getMessage());
    }

    @Test
    public void testSaveAspiration_CreatePrimaryWhenSecondaryExists() {
        String email = "test_user@epam.com";
        Long uid = 1L;
        Authentication authentication= getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);
        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        List<CandidateAspiration> existingAspirations = Collections.singletonList(
                CandidateAspiration.builder().priority(AspirationPriority.ASPIRATION2)
                        .submissionStatus(SubmissionStatus.D).build()
        );
        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, latestVersion)).thenReturn(existingAspirations);
        AspirationDTO aspirationDTO = mock(AspirationDTO.class);
        when(aspirationDTO.isAspiration1()).thenReturn(false);
        when(aspirationDTO.isFutureSkillAcknowledged()).thenReturn(true);
        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.saveAspiration(principal, aspirationDTO);
        });
        assertEquals(ErrorMessages.CREATE_ASPIRATION1, exception.getMessage());
    }



    @Test
    public void testSubmitAspirations_Success() {
        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        SubmitAspirationsRequest submitRequest = new SubmitAspirationsRequest(true);


        Long uid = 1L;
        when(userService.getUuidByEmail("test_user@epam.com")).thenReturn(uid);

        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);

        CandidateAspiration draftP1 = new CandidateAspiration();
        draftP1.setPriority(AspirationPriority.ASPIRATION1);
        draftP1.setSubmissionStatus(SubmissionStatus.D);

        CandidateAspiration draft2 = new CandidateAspiration();
        draft2.setPriority(AspirationPriority.ASPIRATION2);
        draft2.setSubmissionStatus(SubmissionStatus.D);

        List<CandidateAspiration> draftAspirations = List.of(draftP1, draft2);
        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, latestVersion))
                .thenReturn(draftAspirations);

        List<AspirationDTO> response = candidateAspirationService.submitAspirations(principal,submitRequest);

        assertNotNull(response, "The response should not be null");
        verify(candidateAspirationRepository).saveAll(anyList());
    }

    @Test
    public void testSubmitAspirations_IncompleteAspirations() {
        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        SubmitAspirationsRequest submitRequest = new SubmitAspirationsRequest(true);


        Long uid = 1L;
        when(userService.getUuidByEmail("test_user@epam.com")).thenReturn(uid);

        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);

        // Test with only P1 draft
        CandidateAspiration draftP1 = new CandidateAspiration();
        draftP1.setPriority(AspirationPriority.ASPIRATION1);
        draftP1.setSubmissionStatus(SubmissionStatus.D);

        List<CandidateAspiration> incompleteAspirations = List.of(draftP1);
        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, latestVersion))
                .thenReturn(incompleteAspirations);

        assertThrows(CandidateAspirationException.class, () -> candidateAspirationService.submitAspirations(principal,submitRequest));
    }

    @Test
    public void testSubmitAspirations_AlreadySubmitted() {
        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        SubmitAspirationsRequest submitRequest = new SubmitAspirationsRequest(true);

        Long uid = 1L;
        when(userService.getUuidByEmail("test_user@epam.com")).thenReturn(uid);

        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        CandidateAspiration draftP1 = new CandidateAspiration();
        draftP1.setPriority(AspirationPriority.ASPIRATION1);
        draftP1.setSubmissionStatus(SubmissionStatus.S);

        CandidateAspiration draft2 = new CandidateAspiration();
        draft2.setPriority(AspirationPriority.ASPIRATION2);
        draft2.setSubmissionStatus(SubmissionStatus.S);
        List<CandidateAspiration> aspirations = List.of(draftP1, draft2);
        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, latestVersion))
                .thenReturn(aspirations);

        assertThrows(RuntimeException.class, () -> candidateAspirationService.submitAspirations(principal,submitRequest));
    }


    @Test
    public void testEditAspiration_Success() {
        String email = "test_user@epam.com";
        String priority = "ASPIRATION1";
        Long uid = 1L;

        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);

        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        CandidateAspiration existingAspiration = mock(CandidateAspiration.class);
        when(existingAspiration.getAspirationDetail()).thenReturn(mock(AspirationDetail.class));
        when(existingAspiration.getAspirationDetail().getTitle()).thenReturn("Goal1");
        when(existingAspiration.getPriority()).thenReturn(AspirationPriority.ASPIRATION1);
        when(existingAspiration.getSubmissionStatus()).thenReturn(SubmissionStatus.D);

        List<CandidateAspiration> aspirations = List.of(existingAspiration);
        when(candidateAspirationRepository.findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.valueOf(priority), latestVersion)).thenReturn(aspirations);

        AspirationItemDTO aspirationItemDTO = mock(AspirationItemDTO.class);
        when(aspirationItemDTO.getTitle()).thenReturn("Goal1");
        when(aspirationItemDTO.getInputValue()).thenReturn("UpdatedValue");

        AspirationDTO aspirationDTO = mock(AspirationDTO.class);
        when(aspirationDTO.getAspirationList()).thenReturn(List.of(aspirationItemDTO));
        when(aspirationDTO.isAspiration1()).thenReturn(true);
        AspirationDTO result = candidateAspirationService.editAspiration(principal, AspirationPriority.valueOf(priority), aspirationDTO);

        assertNotNull(result, "The result should not be null");
        verify(candidateAspirationRepository).saveAll(aspirations);
    }

    @Test
    public void testEditAspiration_Submitted() {

        String email = "test_user@epam.com";
        String priority = "ASPIRATION1";
        Long uid = 1L;

        Authentication authentication= getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);

        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        CandidateAspiration existingAspiration = mock(CandidateAspiration.class);
        when(existingAspiration.getSubmissionStatus()).thenReturn(SubmissionStatus.S);

        List<CandidateAspiration> aspirations = List.of(existingAspiration);
        when(candidateAspirationRepository.findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.valueOf(priority), latestVersion)).thenReturn(aspirations);


        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.editAspiration(principal, AspirationPriority.valueOf(priority), mock(AspirationDTO.class));
        });

        assertEquals(ErrorMessages.ASPIRATIONS_SUBMITTED, exception.getMessage());

    }


    @Test
    public void testEditAspiration_PriorityModificationNotAllowed() {
        String email = "test_user@epam.com";
        String priority = "ASPIRATION1";
        Long uid = 1L;
        Authentication authentication= getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);

        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);

        CandidateAspiration existingAspiration = mock(CandidateAspiration.class);
        when(existingAspiration.getAspirationDetail()).thenReturn(mock(AspirationDetail.class));
        when(existingAspiration.getAspirationDetail().getTitle()).thenReturn("Goal1");
        when(existingAspiration.getPriority()).thenReturn(AspirationPriority.valueOf("ASPIRATION2"));
        when(existingAspiration.getSubmissionStatus()).thenReturn(SubmissionStatus.D);
        List<CandidateAspiration> aspirations = List.of(existingAspiration);
        when(candidateAspirationRepository.findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.valueOf(priority), latestVersion)).thenReturn(aspirations);

        AspirationItemDTO aspirationItemDTO = mock(AspirationItemDTO.class);
        when(aspirationItemDTO.getTitle()).thenReturn("Goal1");

        AspirationDTO aspirationDTO = mock(AspirationDTO.class);
        when(aspirationDTO.getAspirationList()).thenReturn(List.of(aspirationItemDTO));
        when(aspirationDTO.isAspiration1()).thenReturn(true);

        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.editAspiration(principal, AspirationPriority.valueOf(priority), aspirationDTO);
        });

        assertEquals(ErrorMessages.PRIORITY_MODIFICATION_NOT_ALLOWED, exception.getMessage());
    }


    @Test
    public void testGetAspirations() {
        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        Long uid = 1L;
        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        String previousYear = String.valueOf(LocalDateTime.now().getYear() - 1);

        when(userService.getUuidByEmail(principal.getEmail())).thenReturn(uid);
        TopTalentExcelVersion currentVersion = new TopTalentExcelVersion();
        currentVersion.setUploadedYear(currentYear);
        currentVersion.setVersionName("V1");
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(currentVersion);

        AspirationDetail aspirationDetail1 = new AspirationDetail();
        aspirationDetail1.setId(1L);
        aspirationDetail1.setTitle("Current Aspiration 1");
        aspirationDetail1.setDescription("Current Requirements 1");

        AspirationDetail aspirationDetail2 = new AspirationDetail();
        aspirationDetail2.setId(2L);
        aspirationDetail2.setTitle("Current Aspiration 2");
        aspirationDetail2.setDescription("Current Requirements 2");

        AspirationDetail previousAspirationDetail = new AspirationDetail();
        previousAspirationDetail.setId(3L);
        previousAspirationDetail.setTitle("Previous Aspiration 1");
        previousAspirationDetail.setDescription("Previous Requirements 1");

        TopTalentExcelVersion previousVersion = new TopTalentExcelVersion();
        previousVersion.setUploadedYear(previousYear);
        previousVersion.setVersionName("V1");
        previousVersion.setFileName("STEP_" + previousYear+"_V1.xlsx");

        when(topTalentExcelVersionService.getPreviousYearVersion()).thenReturn(previousVersion);
        List<CandidateAspiration> currentAspirations = List.of(
                CandidateAspiration.builder()
                        .uid(uid)
                        .topTalentExcelVersion(currentVersion)
                        .priority(AspirationPriority.ASPIRATION1)
                        .aspirationDetail(aspirationDetail1)
                        .inputValue("Current Goals 1")
                        .submissionStatus(SubmissionStatus.S)
                        .futureSkillAcknowledged(true)
                        .submitAcknowledged(false)
                        .build(),
                CandidateAspiration.builder()
                        .uid(uid)
                        .topTalentExcelVersion(currentVersion)
                        .priority(AspirationPriority.ASPIRATION2)
                        .aspirationDetail(aspirationDetail2)
                        .inputValue("Current Goals 2")
                        .submissionStatus(SubmissionStatus.S)
                        .futureSkillAcknowledged(true)
                        .submitAcknowledged(false)
                        .build()
        );

        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, currentVersion)).thenReturn(currentAspirations);

        AspirationResponseDTO response = candidateAspirationService.getAspirations(principal);

        assertNotNull(response, "Response should not be null");
        assertFalse(response.getIsFormActive(), "Form should not be active as more than 2 weeks have passed since phase closure");

        assertEquals(2, response.getAspirations().size(), "Mismatch in current year aspirations size");

    }

    @Test
    public void testGetAspirations_DiffVersion_V2() {
        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        Long uid = 1L;
        String currentYear = String.valueOf(LocalDateTime.now().getYear());

        when(userService.getUuidByEmail(principal.getEmail())).thenReturn(uid);
        TopTalentExcelVersion currentVersion = new TopTalentExcelVersion();
        currentVersion.setUploadedYear(currentYear);
        currentVersion.setVersionName("V2");
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(currentVersion);

        AspirationDetail aspirationDetail1 = new AspirationDetail();
        aspirationDetail1.setId(1L);
        aspirationDetail1.setTitle("Current Aspiration 1");
        aspirationDetail1.setDescription("Current Requirements 1");

        AspirationDetail aspirationDetail2 = new AspirationDetail();
        aspirationDetail2.setId(2L);
        aspirationDetail2.setTitle("Current Aspiration 2");
        aspirationDetail2.setDescription("Current Requirements 2");

        AspirationDetail previousAspirationDetail = new AspirationDetail();
        previousAspirationDetail.setId(3L);
        previousAspirationDetail.setTitle("Previous Aspiration 1");
        previousAspirationDetail.setDescription("Previous Requirements 1");

        TopTalentExcelVersion previousVersion = new TopTalentExcelVersion();
        previousVersion.setUploadedYear(currentYear);
        previousVersion.setVersionName("V1");
        previousVersion.setFileName("STEP_" + currentYear+"_V1.xlsx");

        List<CandidateAspiration> currentAspirations = List.of(
                CandidateAspiration.builder()
                        .uid(uid)
                        .topTalentExcelVersion(currentVersion)
                        .priority(AspirationPriority.ASPIRATION1)
                        .aspirationDetail(aspirationDetail1)
                        .inputValue("Current Goals 1")
                        .submissionStatus(SubmissionStatus.S)
                        .futureSkillAcknowledged(true)
                        .submitAcknowledged(false)
                        .build(),
                CandidateAspiration.builder()
                        .uid(uid)
                        .topTalentExcelVersion(currentVersion)
                        .priority(AspirationPriority.ASPIRATION2)
                        .aspirationDetail(aspirationDetail2)
                        .inputValue("Current Goals 2")
                        .submissionStatus(SubmissionStatus.S)
                        .futureSkillAcknowledged(true)
                        .submitAcknowledged(false)
                        .build()
        );

        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, currentVersion)).thenReturn(currentAspirations);
        when(topTalentExcelVersionService.getPreviousYearVersion()).thenReturn(previousVersion);
        AspirationResponseDTO response = candidateAspirationService.getAspirations(principal);

        assertNotNull(response, "Response should not be null");
        assertFalse(response.getIsFormActive(), "Form should not be active as more than 2 weeks have passed since phase closure");

        assertEquals(2, response.getAspirations().size(), "Mismatch in current year aspirations size");

    }

    @Test
    public void testGetAspirations_PrevYearNotFound() {
        Authentication authentication= getMockAuthentication(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        Long uid = 1L;
        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        when(userService.getUuidByEmail(principal.getEmail())).thenReturn(uid);
        TopTalentExcelVersion currentVersion = new TopTalentExcelVersion();
        currentVersion.setUploadedYear(currentYear);
        currentVersion.setVersionName("V1");
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(currentVersion);

        AspirationDetail aspirationDetail1 = new AspirationDetail();
        aspirationDetail1.setId(1L);
        aspirationDetail1.setTitle("Current Aspiration 1");
        aspirationDetail1.setDescription("Current Requirements 1");

        AspirationDetail aspirationDetail2 = new AspirationDetail();
        aspirationDetail2.setId(2L);
        aspirationDetail2.setTitle("Current Aspiration 2");
        aspirationDetail2.setDescription("Current Requirements 2");

        AspirationDetail previousAspirationDetail = new AspirationDetail();
        previousAspirationDetail.setId(3L);
        previousAspirationDetail.setTitle("Previous Aspiration 1");
        previousAspirationDetail.setDescription("Previous Requirements 1");

        when(topTalentExcelVersionService.getPreviousYearVersion()).thenReturn(TopTalentExcelVersion.builder()
                .fileName("NA")
                .uploadedYear("NA")
                .versionName("NA")
                .build());

        List<CandidateAspiration> currentAspirations = List.of(
                CandidateAspiration.builder()
                        .uid(uid)
                        .topTalentExcelVersion(currentVersion)
                        .priority(AspirationPriority.ASPIRATION1)
                        .aspirationDetail(aspirationDetail1)
                        .inputValue("Current Goals 1")
                        .submissionStatus(SubmissionStatus.S)
                        .futureSkillAcknowledged(true)
                        .submitAcknowledged(false)
                        .build(),
                CandidateAspiration.builder()
                        .uid(uid)
                        .topTalentExcelVersion(currentVersion)
                        .priority(AspirationPriority.ASPIRATION2)
                        .aspirationDetail(aspirationDetail2)
                        .inputValue("Current Goals 2")
                        .submissionStatus(SubmissionStatus.S)
                        .futureSkillAcknowledged(true)
                        .submitAcknowledged(false)
                        .build()
        );

        when(candidateAspirationRepository.findByUidAndTopTalentExcelVersion(uid, currentVersion)).thenReturn(currentAspirations);

        AspirationResponseDTO response = candidateAspirationService.getAspirations(principal);

        assertNotNull(response, "Response should not be null");
        assertFalse(response.getIsFormActive(), "Form should not be active as more than 2 weeks have passed since phase closure");

        assertEquals(2, response.getAspirations().size(), "Mismatch in current year aspirations size");

    }


    @Test
    public void testEditAspiration_PriorityModificationNotAllowed_P2() {
        String email = "test_user@epam.com";
        String priority = "ASPIRATION2";
        Long uid = 1L;
        Authentication authentication= getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        when(userService.getUuidByEmail(email)).thenReturn(uid);

        TopTalentExcelVersion latestVersion = mock(TopTalentExcelVersion.class);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);

        CandidateAspiration existingAspiration = mock(CandidateAspiration.class);
        when(existingAspiration.getAspirationDetail()).thenReturn(mock(AspirationDetail.class));
        when(existingAspiration.getAspirationDetail().getTitle()).thenReturn("Goal1");
        when(existingAspiration.getPriority()).thenReturn(AspirationPriority.valueOf("ASPIRATION1"));
        when(existingAspiration.getSubmissionStatus()).thenReturn(SubmissionStatus.D);

        List<CandidateAspiration> aspirations = List.of(existingAspiration);
        when(candidateAspirationRepository.findByUidAndPriorityAndTopTalentExcelVersion(uid, AspirationPriority.valueOf(priority), latestVersion)).thenReturn(aspirations);

        AspirationItemDTO aspirationItemDTO = mock(AspirationItemDTO.class);
        when(aspirationItemDTO.getTitle()).thenReturn("Goal1");

        AspirationDTO aspirationDTO = mock(AspirationDTO.class);
        when(aspirationDTO.getAspirationList()).thenReturn(List.of(aspirationItemDTO));
        when(aspirationDTO.isAspiration1()).thenReturn(false);

        CandidateAspirationException exception = assertThrows(CandidateAspirationException.class, () -> {
            candidateAspirationService.editAspiration(principal, AspirationPriority.valueOf(priority), aspirationDTO);
        });

        assertEquals(ErrorMessages.PRIORITY_MODIFICATION_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void testGetSubmittedAspirationsForApproval() {
        AspirationDetail aspirationDetail1 = AspirationDetail.builder()
                .id(1L)
                .title("Solution Architect")
                .description("Aspiring to become a Solution Architect.")
                .build();

        CandidateAspiration mockAspiration = CandidateAspiration.builder()
                .uid(123L)
                .aspirationDetail(aspirationDetail1)
                .inputValue("I want to lead architecture design efforts.")
                .submissionStatus(SubmissionStatus.S)
                .build();


        AspirationPriorityDTO mockPriorityDTO = AspirationPriorityDTO.builder()
                .priority(null)
                .aspirationList(List.of())
                .submissionStatus(SubmissionStatus.S)
                .build();

        AspirationResponseDTO mockResponseDTO = AspirationResponseDTO.builder()
                .isFormActive(true)
                .aspirationExplanation(List.of("Solution Architect: Aspiring to become a Solution Architect."))
                .aspirations(List.of(mockPriorityDTO))
                .previousYearAspirations(List.of())
                .build();

        when(candidateAspirationRepository.findBySubmissionStatus(SubmissionStatus.S))
                .thenReturn(List.of(mockAspiration));

        when(mapper.mapToAspirationResponseDTO(mockAspiration)).thenReturn(mockResponseDTO);

        List<AspirationResponseDTO> result = aspirationService.getSubmittedAspirationsForApproval();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsFormActive());
        assertEquals("Solution Architect: Aspiring to become a Solution Architect.", result.get(0).getAspirationExplanation().get(0));
        verify(candidateAspirationRepository, times(1)).findBySubmissionStatus(SubmissionStatus.S);
        verify(mapper, times(1)).mapToAspirationResponseDTO(mockAspiration);
    }


    @Test
    void testApproveAspiration() {
        AspirationDetail aspirationDetail1 = AspirationDetail.builder()
                .id(1L)
                .title("Solution Architect")
                .description("Aspiring to become a Solution Architect.")
                .build();

        CandidateAspiration mockAspiration = CandidateAspiration.builder()
                .uid(123L)
                .aspirationDetail(aspirationDetail1)
                .inputValue("I want to lead architecture design efforts.")
                .submissionStatus(SubmissionStatus.S)
                .build();


        AspirationApprovalRequestDTO approvalRequest = new AspirationApprovalRequestDTO();
        approvalRequest.setAssignedRole("Solution Architect");
        approvalRequest.setProficiency("Advanced");
        approvalRequest.setApprovedBy("John Doe");

        when(candidateAspirationRepository.findByUidAndAspirationDetailId(123L, 1L))
                .thenReturn(Optional.of(mockAspiration));

        aspirationService.approveAspiration(123L, 1L, approvalRequest);

        verify(candidateAspirationRepository, times(1)).save(mockAspiration);

        assertEquals("Solution Architect", mockAspiration.getAssignedRole());
        assertEquals("Advanced", mockAspiration.getProficiency());
        assertEquals(SubmissionStatus.A, mockAspiration.getSubmissionStatus());
        assertEquals("John Doe", mockAspiration.getApprovedBy());
    }
}





