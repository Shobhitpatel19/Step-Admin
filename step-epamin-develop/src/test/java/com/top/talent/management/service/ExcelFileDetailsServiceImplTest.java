package com.top.talent.management.service;


import com.top.talent.management.dto.ExcelFileDetailsDTO;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.service.impl.ExcelFileDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.top.talent.management.constants.Constants.CULTURAL_SCORE;
import static com.top.talent.management.constants.Constants.CULTURAL_SCORE_SUFFIX;
import static com.top.talent.management.constants.Constants.CULTURE_SCORE_REQUIRED_HEADERS;
import static com.top.talent.management.constants.Constants.HEROES;
import static com.top.talent.management.constants.Constants.HEROES_REQUIRED_HEADERS;
import static com.top.talent.management.constants.Constants.HEROES_SUFFIX;
import static com.top.talent.management.constants.Constants.REQUIRED_COLUMNS;
import static com.top.talent.management.constants.Constants.STEP;
import static com.top.talent.management.constants.Constants.STEP_SUFFIX;
import static com.top.talent.management.constants.Constants.VERSION_1_PREFIX;
import static com.top.talent.management.constants.Constants.VERSION_PREFIX;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExcelFileDetailsServiceImplTest {

    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;


    @InjectMocks
    private ExcelFileDetailsServiceImpl generateExcelFileDetailsService;


    private TopTalentExcelVersion excelVersion;

    @BeforeEach
    void setup() {
        excelVersion = new TopTalentExcelVersion();
        excelVersion.setVersionName("V2");
        excelVersion.setUploadedYear(String.valueOf(LocalDateTime.now().getYear()));
    }

    @Test
    void testGenerateExcelFileDetailsForStepWithIncrementVersion() throws VersionException {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(excelVersion);
        ExcelFileDetailsDTO result = generateExcelFileDetailsService.generateExcelFile(STEP);

        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        String expectedFileName = STEP_SUFFIX + currentYear + VERSION_PREFIX + "3";

        assertEquals(expectedFileName, result.getExcelName());
        assertEquals(REQUIRED_COLUMNS, result.getHeaders());
    }

    @Test
    void testGenerateExcelFileDetailsForStepNewYearIncrementVersion() throws VersionException {
        excelVersion.setUploadedYear("2020");
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(excelVersion);
        ExcelFileDetailsDTO result = generateExcelFileDetailsService.generateExcelFile(STEP);

        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        String expectedFileName = STEP_SUFFIX + currentYear + VERSION_1_PREFIX;

        assertEquals(expectedFileName, result.getExcelName());
        assertEquals(REQUIRED_COLUMNS, result.getHeaders());
    }

    @Test
    void testGenerateExcelFileDetailsForHeroesWithoutIncrement() throws VersionException {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(excelVersion);
        ExcelFileDetailsDTO result = generateExcelFileDetailsService.generateExcelFile(HEROES);

        String year = excelVersion.getUploadedYear();
        String expectedFileName = STEP_SUFFIX + HEROES_SUFFIX + year + "_" + "V2";

        assertEquals(expectedFileName, result.getExcelName());
        assertEquals(HEROES_REQUIRED_HEADERS, result.getHeaders());
    }

    @Test
    void testGenerateExcelFileDetailsForCulturalScoreWithoutIncrement() throws VersionException {
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(excelVersion);
        ExcelFileDetailsDTO result = generateExcelFileDetailsService.generateExcelFile(CULTURAL_SCORE);

        String year = excelVersion.getUploadedYear();
        String expectedFileName = STEP_SUFFIX + CULTURAL_SCORE_SUFFIX + year + "_" + "V2";

        assertEquals(expectedFileName, result.getExcelName());
        assertEquals(CULTURE_SCORE_REQUIRED_HEADERS, result.getHeaders());
    }

    @Test
    void testGenerateExcelFileDetailsForInvalidExcelType() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            generateExcelFileDetailsService.generateExcelFile("Invalid Type");
        });

        assertEquals("Invalid Excel Type", thrown.getMessage());

    }

    @Test
    void testGenerateExcelFileDetailsWhenExceptionThrown() throws VersionException {
        when(topTalentExcelVersionService.findLatestVersion()).thenThrow(new VersionException("Database error"));
        ExcelFileDetailsDTO result = generateExcelFileDetailsService.generateExcelFile(STEP);

        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        String expectedFileName = STEP_SUFFIX + currentYear + VERSION_1_PREFIX;

        assertEquals(expectedFileName, result.getExcelName());
        assertEquals(REQUIRED_COLUMNS, result.getHeaders());
    }
}