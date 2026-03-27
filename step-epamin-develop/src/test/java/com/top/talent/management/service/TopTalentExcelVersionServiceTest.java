package com.top.talent.management.service;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.repository.TopTalentExcelVersionRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.TopTalentExcelVersionServiceImpl;
import com.top.talent.management.utils.TopTalentEmployeeTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopTalentExcelVersionServiceTest {

    @Mock
    private TopTalentExcelVersionRepository topTalentExcelVersionRepository;

    @InjectMocks
    private TopTalentExcelVersionServiceImpl topTalentExcelVersionService;

    @Test
    void saveVersionSuccess() {
        String fileName = "STEP_2024_V1.xlsx";
        String versionName = "V1";
        String year = String.valueOf(LocalDateTime.now().getYear());
        CustomUserPrincipal customUserPrincipal = mock(CustomUserPrincipal.class);
        when(customUserPrincipal.getFullName()).thenReturn("John Doe");

        TopTalentExcelVersion version = TopTalentEmployeeTestUtils.createVersion(fileName, year, versionName);

        when(topTalentExcelVersionRepository.save(any(TopTalentExcelVersion.class))).thenReturn(version);

        TopTalentExcelVersion savedVersion = topTalentExcelVersionService.saveVersion(fileName, customUserPrincipal, versionName, year);

        assertNotNull(savedVersion);
        assertEquals(fileName, savedVersion.getFileName());
        assertEquals(versionName, savedVersion.getVersionName());
        assertEquals(year, savedVersion.getUploadedYear());
        verify(topTalentExcelVersionRepository, times(1)).save(any(TopTalentExcelVersion.class));
    }

    @Test
    void deleteVersionSuccess() {
        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName("STEP_2024_V1.xlsx");

        topTalentExcelVersionService.deleteVersion(version);

        verify(topTalentExcelVersionRepository, times(1)).delete(version);
    }

    @Test
    void checkIfFileExistsReturnsTrue() {
        String fileName = "STEP_2024_V1.xlsx";
        when(topTalentExcelVersionRepository.existsByFileName(fileName)).thenReturn(true);

        boolean fileExists = topTalentExcelVersionService.checkIfFileExists(fileName);

        assertTrue(fileExists);
    }

    @Test
    void checkIfFileExistsReturnsFalse() {
        String fileName = "STEP_2024_V1.xlsx";
        when(topTalentExcelVersionRepository.existsByFileName(fileName)).thenReturn(false);

        boolean fileExists = topTalentExcelVersionService.checkIfFileExists(fileName);

        assertFalse(fileExists);
    }

    @Test
    void findLatestVersion_ThrowsNoVersionFound() {
        String currentYear = String.valueOf(LocalDateTime.now().getYear());

        when(topTalentExcelVersionRepository.findAllByUploadedYear(currentYear)).thenReturn(Collections.emptyList());

        VersionException exception = assertThrows(VersionException.class, () -> topTalentExcelVersionService.findLatestVersion());

        assertEquals(ErrorMessages.LIST_NOT_FOUND, exception.getMessage());
    }

    @Test
    void findLatestVersionSuccess() {
        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName("STEP_" + currentYear + "_V1.xlsx");
        version.setUploadedYear(currentYear);

        when(topTalentExcelVersionRepository.findAllByUploadedYear(currentYear)).thenReturn(Collections.singletonList(version));

        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();

        assertNotNull(latestVersion);
        assertEquals(version.getFileName(), latestVersion.getFileName());
    }

    @Test
    void testGetPreviousYearVersion_WithPreviousVersion_Version1() {
        TopTalentExcelVersion latestVersion = TopTalentExcelVersion.builder()
                .versionName("V1")
                .uploadedYear("2025")
                .fileName("STEP_2025_V1")
                .created(LocalDateTime.now())
                .build();
        when(topTalentExcelVersionRepository.findAllByUploadedYear("2025"))
                .thenReturn(Collections.singletonList(latestVersion));



        TopTalentExcelVersion expectedVersion =
                TopTalentExcelVersion.builder()
                        .versionName("V1")
                        .uploadedYear("2024")
                        .fileName("STEP_2024_V1")
                        .created(LocalDateTime.now().minusDays(80))
                        .build();
        when(topTalentExcelVersionRepository.findAllByUploadedYear("2024"))
                .thenReturn(Collections.singletonList(expectedVersion));

        TopTalentExcelVersion result = topTalentExcelVersionService.getPreviousYearVersion();

        assertEquals(expectedVersion, result);

    }

    @Test
    void testGetPreviousYearVersion_WithPreviousVersion_NotVersion1() {
        TopTalentExcelVersion latestVersion = TopTalentExcelVersion.builder()
                .versionName("V2")
                .uploadedYear("2025")
                .fileName("STEP_2025_V2")
                .created(LocalDateTime.now())
                .build();


        TopTalentExcelVersion expectedVersion =
                TopTalentExcelVersion.builder()
                        .versionName("V1")
                        .uploadedYear("2025")
                        .fileName("STEP_2025_V1")
                        .created(LocalDateTime.now().minusDays(80))
                        .build();
        when(topTalentExcelVersionRepository.findAllByUploadedYear("2025"))
                .thenReturn(List.of(latestVersion,expectedVersion));

        TopTalentExcelVersion result = topTalentExcelVersionService.getPreviousYearVersion();

        assertEquals(expectedVersion, result);
    }

    @Test
    void testGetPreviousYearVersion_WhenNoPreviousVersionFound() {
        when(topTalentExcelVersionRepository.findAllByUploadedYear(Mockito.anyString()))
                .thenReturn(Collections.emptyList());

        TopTalentExcelVersion result = topTalentExcelVersionService.getPreviousYearVersion();


        assertEquals("NA", result.getFileName());
        assertEquals("NA", result.getUploadedYear());
        assertEquals("NA", result.getVersionName());
    }


}
