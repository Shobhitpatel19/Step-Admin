package com.top.talent.management.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.top.talent.management.dto.CulturalScore;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.CultureScoreException;
import com.top.talent.management.exception.EmptyFileException;
import com.top.talent.management.exception.InvalidCandidateException;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.TopTalentExcelVersionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CulturalScoreListenerTest {

    @Mock
    private CustomUserPrincipal customUserPrincipal;
    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;

    @InjectMocks
    private CulturalScoreListener culturalScoreListener;

    private AnalysisContext context;
    private  final TopTalentExcelVersion topTalentExcelVersion = mock(TopTalentExcelVersion.class);

    @BeforeEach
    public void setup() {
        culturalScoreListener = new CulturalScoreListener("STEP_CULTURAL_SCORE_2025_V1.xlsx", customUserPrincipal, topTalentExcelVersionService);
        context = mock(AnalysisContext.class);
    }

    @Test
     void shouldAddValidCulturalScore() {
        CulturalScore validScore = new CulturalScore(123456L, 3.0);
        assertDoesNotThrow(() -> culturalScoreListener.invoke(validScore, context));
        assertTrue(culturalScoreListener.getDataList().contains(validScore));
    }


    @Test
    void shouldCreateTopTalentExcelVersionIfNull() {
        String fileName = "STEP_CULTURAL_SCORE_2025_V1.xlsx";
        String extractedVersionName = "V1";
        String extractedYear = "2025";
        CulturalScore culturalScore = new CulturalScore(123456L, 5.0);

        when(topTalentExcelVersionService.saveVersion(fileName, customUserPrincipal, extractedVersionName, extractedYear))
                .thenReturn(topTalentExcelVersion);

        assertNull(culturalScoreListener.getTopTalentExcelVersion(), "TopTalentExcelVersion should initially be null");

        culturalScoreListener.invoke(culturalScore, context);

        assertNotNull(culturalScoreListener.getTopTalentExcelVersion(), "TopTalentExcelVersion should not be null after invocation");
        verify(topTalentExcelVersionService).saveVersion(fileName, customUserPrincipal, extractedVersionName, extractedYear);
    }


    @Test
    void shouldNotCreateTopTalentExcelVersionIfNotNull() throws NoSuchFieldException, IllegalAccessException {
        Field versionField = CulturalScoreListener.class.getDeclaredField("topTalentExcelVersion");
        versionField.setAccessible(true);
        versionField.set(culturalScoreListener, topTalentExcelVersion);
        CulturalScore culturalScore = new CulturalScore(123456L, 5.0);

        verify(topTalentExcelVersionService, never()).saveVersion(anyString(), any(CustomUserPrincipal.class), anyString(), anyString());

        assertNotNull(culturalScoreListener.getTopTalentExcelVersion(), "TopTalentExcelVersion should not be null before invoke");
        culturalScoreListener.invoke(culturalScore, context);

        verify(topTalentExcelVersionService, never()).saveVersion(anyString(), any(CustomUserPrincipal.class), anyString(), anyString());

        assertTrue(culturalScoreListener.getDataList().contains(culturalScore));
        assertTrue(culturalScoreListener.getUidSet().contains(culturalScore.getUid()));
    }

    @Test
     void shouldThrowDuplicateUidExceptionWhenUidAlreadyExists() {
        CulturalScore score = new CulturalScore(123456L, 3.0);
        culturalScoreListener.invoke(score, context); // first time should work
        assertThrows(CultureScoreException.class, () -> culturalScoreListener.invoke(score, context));
    }

    @Test
    void shouldThrowExceptionWhenUidIsNull() {
        CulturalScore score = new CulturalScore(null, 3.0);
        assertThrows(CultureScoreException.class, () -> culturalScoreListener.invoke(score, context));
    }

    @Test
    void shouldThrowInvalidCandidateExceptionWhenUidIsNotProperFormat() {
        CulturalScore score = new CulturalScore(123L, 3.0);  // UID does not match required format
        assertThrows(InvalidCandidateException.class, () -> culturalScoreListener.invoke(score, context));
    }

    @Test
    void shouldThrowExceptionWhenCultureScoreFromFeedbackIsNull() {
        CulturalScore score = new CulturalScore(123456L, null);
        assertThrows(CultureScoreException.class, () -> culturalScoreListener.invoke(score, context));
    }


    @Test
    void shouldNotThrowAnyExceptionWhenDataIsValid() {
        culturalScoreListener.getDataList().add(new CulturalScore(123456L, 4.0)); // valid data
        assertDoesNotThrow(() -> culturalScoreListener.doAfterAllAnalysed(context));
    }

    @Test
    void shouldThrowEmptyFileExceptionWhenNoDataIsProcessed() {
        assertThrows(EmptyFileException.class, () -> culturalScoreListener.doAfterAllAnalysed(context));
    }

    @Test
    void shouldThrowCultureScoreExceptionForMoreThanThreeInvalidRows() {
        culturalScoreListener.getInvalidRows().addAll(java.util.List.of("bad row 1", "bad row 2", "bad row 3", "bad row 4"));
        culturalScoreListener.getDataList().add(new CulturalScore(123456L, 4.0));  // Add valid data to pass the empty file check

        assertThrows(CultureScoreException.class, () -> culturalScoreListener.doAfterAllAnalysed(context));
    }

    @Test
    void shouldThrowCultureScoreExceptionWithInvalidRowsListed() {
        culturalScoreListener.getInvalidRows().addAll(java.util.List.of("bad row 1", "bad row 2", "bad row 3"));
        culturalScoreListener.getDataList().add(new CulturalScore(123456L, 4.0));  // Add valid data to pass the empty file check

        CultureScoreException exception = assertThrows(CultureScoreException.class, () -> culturalScoreListener.doAfterAllAnalysed(context));
        Set<String> expected = new HashSet<>(Arrays.asList("bad row 1", "bad row 2", "bad row 3"));
        Set<String> actual = new HashSet<>(Arrays.asList(exception.getMessage().replace("[", "").replace("]", "").split(", ")));

        assertEquals(expected, actual);
    }
}