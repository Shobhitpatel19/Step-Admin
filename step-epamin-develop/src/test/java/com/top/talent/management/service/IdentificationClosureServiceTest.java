package com.top.talent.management.service;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.entity.IdentificationClosure;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.repository.IdentificationClosureRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.IdentificationClosureServiceImpl;
import com.top.talent.management.service.impl.TopTalentExcelVersionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class IdentificationClosureServiceTest {

    @Mock
    private IdentificationClosureRepository identificationClosureRepository;

    @Mock
    private TopTalentExcelVersionServiceImpl topTalentExcelVersionService;

    @InjectMocks
    private IdentificationClosureServiceImpl idPhaseClosureService;

    private TopTalentExcelVersion topTalentExcelVersion;
    private CustomUserPrincipal customUserPrincipal;
    private List<IdentificationClosure> phases;

    @BeforeEach
    void setUp() {
        topTalentExcelVersion = new TopTalentExcelVersion();
        topTalentExcelVersion.setId(1L);
        customUserPrincipal = mock(CustomUserPrincipal.class);

        TopTalentExcelVersion olderVersion = new TopTalentExcelVersion();
        IdentificationClosure latestClosure = createPhaseClosure(topTalentExcelVersion, LocalDateTime.now());
        IdentificationClosure olderClosure = createPhaseClosure(olderVersion, LocalDateTime.now().minusDays(1));

        phases = Arrays.asList(latestClosure, olderClosure);
    }

    private IdentificationClosure createPhaseClosure(TopTalentExcelVersion version, LocalDateTime endedAt) {
        return IdentificationClosure.builder()
                .topTalentExcelVersion(version)
                .endedAt(endedAt)
                .build();
    }

    @Test
    void testEndPhase() {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(topTalentExcelVersion);
        idPhaseClosureService.endPhase(customUserPrincipal);

        verify(identificationClosureRepository, times(1)).save(any(IdentificationClosure.class));
        verify(topTalentExcelVersionService, times(1)).findLatestVersion();
    }

    @Test
    void testFindLatestPhaseEmpty() {
        when(identificationClosureRepository.findAll()).thenReturn(List.of());
       VersionException exception = assertThrows(VersionException.class,
               () -> idPhaseClosureService.findLatestPhase());
        assertEquals(ErrorMessages.NO_PHASES_FOUND, exception.getMessage());
    }

    @Test
    void testEndPhase_NoVersion() {
        when(topTalentExcelVersionService.findLatestVersion()).thenThrow(new VersionException(ErrorMessages.LIST_NOT_FOUND));

        VersionException exception = assertThrows(VersionException.class, () -> idPhaseClosureService.endPhase(customUserPrincipal));
        assertEquals(ErrorMessages.LIST_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testFindLatestPhase() {
        when(identificationClosureRepository.findAll()).thenReturn(phases);

        TopTalentExcelVersion result = idPhaseClosureService.findLatestPhase();
        assertEquals(topTalentExcelVersion, result);
    }


    @Test
    void testIsPhaseClosedSameVersion() {
        when(identificationClosureRepository.findAll()).thenReturn(phases);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(topTalentExcelVersion);

        assertTrue(idPhaseClosureService.isPhaseClosed());
    }

    @Test
    void testIsPhaseClosedDifferentVersion() {
        TopTalentExcelVersion differentVersion = new TopTalentExcelVersion(
                "v1.xlsx", "v1", LocalDateTime.now(), "doahd", LocalDateTime.now(), "kjhda", "2025"
        );

        when(identificationClosureRepository.findAll()).thenReturn(phases);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(differentVersion);

        assertFalse(idPhaseClosureService.isPhaseClosed());
    }
    @Test
    void testLatestPhaseEndDate_Success() {
        LocalDateTime now = LocalDateTime.now();

        phases = Arrays.asList(
                IdentificationClosure.builder().endedAt(now.minusDays(5)).build(),
                IdentificationClosure.builder().endedAt(now.minusDays(3)).build(),
                IdentificationClosure.builder().endedAt(now).build()  
        );

        when(identificationClosureRepository.findAll()).thenReturn(phases);

        // Execute
        LocalDateTime latestPhaseEndTime = idPhaseClosureService.latestPhaseEndDate();

        // Verify
        assertEquals(now, latestPhaseEndTime);
    }

    @Test
    void testLatestPhaseEndDate_NoPhases_Failure() {
        when(identificationClosureRepository.findAll()).thenReturn(new ArrayList<>());

        // Execute and Verify
        VersionException thrown = assertThrows(VersionException.class, () -> {
            idPhaseClosureService.latestPhaseEndDate();
        });

        assertEquals(ErrorMessages.LIST_NOT_FOUND, thrown.getMessage());
    }

    @Test
    void testIsFormClosedWhenPhaseEndedAndNoOfWeeksGreater() {
        TopTalentExcelVersion topTalentExcelVersion1 = mock(TopTalentExcelVersion.class);
        IdentificationClosure identificationClosure = IdentificationClosure.builder()
                .topTalentExcelVersion(topTalentExcelVersion1)
                .endedAt(LocalDateTime.now().minusWeeks(1))
                .build();
        when(identificationClosureRepository.findAll()).thenReturn(List.of(identificationClosure));
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(topTalentExcelVersion1);
        assertFalse(idPhaseClosureService.isFormClosed(2));
    }

    @Test
    void testIsFormClosedWhenPhaseEndedAndNoOfWeeksEqual() {
        TopTalentExcelVersion topTalentExcelVersion1 = mock(TopTalentExcelVersion.class);
        IdentificationClosure identificationClosure = IdentificationClosure.builder()
                .topTalentExcelVersion(topTalentExcelVersion1)
                .endedAt(LocalDateTime.now().minusWeeks(2))
                .build();
        when(identificationClosureRepository.findAll()).thenReturn(List.of(identificationClosure));
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(topTalentExcelVersion1);
        assertFalse(idPhaseClosureService.isFormClosed(2));
    }

    @Test
    void testIsFormClosedWhenPhaseEndedAndNoOfWeeksLesser() {
        TopTalentExcelVersion topTalentExcelVersion1 = mock(TopTalentExcelVersion.class);
        IdentificationClosure identificationClosure = IdentificationClosure.builder()
                .topTalentExcelVersion(topTalentExcelVersion1)
                .endedAt(LocalDateTime.now().minusWeeks(4))
                .build();
        when(identificationClosureRepository.findAll()).thenReturn(List.of(identificationClosure));
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(topTalentExcelVersion1);
        assertTrue(idPhaseClosureService.isFormClosed(2));
    }


    @Test
    void testIsFormClosedWhenPhaseIsNotClosed() {
        TopTalentExcelVersion topTalentExcelVersion1 = mock(TopTalentExcelVersion.class);
        IdentificationClosure identificationClosure = IdentificationClosure.builder()
                        .topTalentExcelVersion(mock(TopTalentExcelVersion.class))
                                .build();
        when(identificationClosureRepository.findAll()).thenReturn(List.of(identificationClosure));
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(topTalentExcelVersion1);
        assertFalse(idPhaseClosureService.isFormClosed(2));
    }

    @Test
    void testIsFormClosedThrowsExceptionWhenLatestPhaseNotFound() {
        when(identificationClosureRepository.findAll()).thenReturn(List.of());

        assertThrows(VersionException.class, () -> idPhaseClosureService.isFormClosed(2));
    }
}
