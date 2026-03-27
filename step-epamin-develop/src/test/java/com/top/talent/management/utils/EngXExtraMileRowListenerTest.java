package com.top.talent.management.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.dto.EngXExtraMileRating;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.EmptyFileException;
import com.top.talent.management.exception.EngXExtraMileRatingException;
import com.top.talent.management.exception.InvalidCandidateException;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.TopTalentExcelVersionService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EngXExtraMileRowListenerTest {

    private EngXExtraMileRowListener listener;

    @Mock
    private CustomUserPrincipal customUserPrincipal;

    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;

    @Mock
    private AnalysisContext analysisContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new EngXExtraMileRowListener("STEP_HEROES_2024_V1.xlsx", customUserPrincipal, topTalentExcelVersionService);
    }

    @Test
    void testInvokeValidRow() {

        listener.invoke(getMockRow(123456L,3,4), analysisContext);

        assertEquals(1, listener.getRatingList().size());
        assertEquals(1, listener.getUidSet().size());
        verify(topTalentExcelVersionService, times(1)).saveVersion(anyString(), any(CustomUserPrincipal.class), anyString(), anyString());
    }
    @Test
    void testInvokeNullUID() {

        listener.invoke(getMockRow(123456L,4,3), analysisContext);

        EngXExtraMileRating row = new EngXExtraMileRating();
        row.setUid(null);
        row.setEngXRating(4L);
        row.setExtraMileRating(3L);

        EngXExtraMileRatingException exception = assertThrows(EngXExtraMileRatingException.class, () -> listener.invoke(row, analysisContext));
        assertEquals(ErrorMessages.NULL_UID, exception.getMessage());
    }

    @Test
    void testInvokeDuplicateUID() {

        listener.invoke(getMockRow(123456L,4,3), analysisContext);

        EngXExtraMileRating row = getMockRow(123456L,4,3);

        EngXExtraMileRatingException exception = assertThrows(EngXExtraMileRatingException.class, () -> listener.invoke(row, analysisContext));
        assertEquals(ErrorMessages.DUPLICATE_UID + "123456", exception.getMessage());
    }

    @Test
    void testInvalidUidFormat() {
        listener.invoke(getMockRow(123456L,4,3), analysisContext);

        EngXExtraMileRating row = getMockRow(1234L,4L,3L);

        InvalidCandidateException exception = Assert.assertThrows(InvalidCandidateException.class, () -> {
            listener.invoke(row,analysisContext);
        });
        Assert.assertEquals(ErrorMessages.INVALID_UID, exception.getMessage());
    }

    @Test
    void testNullEngxRating() {
        listener.invoke(getMockRow(123456L,4,3), analysisContext);

        EngXExtraMileRating row = new EngXExtraMileRating();
        row.setUid(138456L);
        row.setEngXRating(null);
        row.setExtraMileRating(3L);

        EngXExtraMileRatingException exception = assertThrows(EngXExtraMileRatingException.class, () -> {
            listener.invoke(row,analysisContext);
        });
        assertEquals(ErrorMessages.NULL_RATING_VALUE+row.getUid(), exception.getMessage());
    }

    @Test
    void testExtraMileRatingGreaterThan4() {
        listener.invoke(getMockRow(123456L,4,3), analysisContext);

        listener.invoke(getMockRow(138456L,3L,109L),analysisContext);

        EngXExtraMileRatingException exception = Assert.assertThrows(EngXExtraMileRatingException.class, () -> {

            listener.doAfterAllAnalysed(analysisContext);
        });
        assertEquals(ErrorMessages.EXTRAMILE_INVALID_RATING_VALUE + 138456L, exception.getMessage());
    }
    @Test
    void testEngxRatingLessThan0() {
        listener.invoke(getMockRow(123456L,4,3), analysisContext);

        listener.invoke(getMockRow(138456L,-7L,1L),analysisContext);

        EngXExtraMileRatingException exception = Assert.assertThrows(EngXExtraMileRatingException.class, () -> {

            listener.doAfterAllAnalysed(analysisContext);
        });
        Assert.assertEquals(ErrorMessages.ENGX_INVALID_RATING_VALUE + 138456L, exception.getMessage());
    }

    @Test
    void testMultipleWrongRatings() {
        listener.invoke(getMockRow(123456L,4,3), analysisContext);
        listener.invoke(getMockRow(130856L,3L,9L),analysisContext);
        listener.invoke(getMockRow(138856L, 8L, 1L),analysisContext);
        listener.invoke(getMockRow(170856L,3L,-3L),analysisContext);
        listener.invoke(getMockRow(138456L,-7L,1),analysisContext);

        EngXExtraMileRatingException exception = Assert.assertThrows(EngXExtraMileRatingException.class, () -> {
            listener.doAfterAllAnalysed(analysisContext);
        });

        assertEquals( ErrorMessages.ENGX_INVALID_RATING_VALUE + "138456, " + ErrorMessages.EXTRAMILE_INVALID_RATING_VALUE + "170856, " + ErrorMessages.ENGX_INVALID_RATING_VALUE + "138856, " + ErrorMessages.EXTRAMILE_INVALID_RATING_VALUE + "130856", exception.getMessage());
    }

    @Test
    void testDoAfterAllAnalysedEmptyList() {
        EmptyFileException exception = assertThrows(EmptyFileException.class, () -> listener.doAfterAllAnalysed(analysisContext));
        assertEquals(ErrorMessages.EMPTY_FILE, exception.getMessage());
    }

    @Test
    void testDoAfterAllAnalysedNonEmptyList() {
        EngXExtraMileRating row = new EngXExtraMileRating();
        row.setUid(1L);
        row.setEngXRating(4L);
        row.setExtraMileRating(3L);

        listener.getRatingList().add(row);

        listener.doAfterAllAnalysed(analysisContext);

        assertDoesNotThrow(() -> listener.doAfterAllAnalysed(analysisContext));
    }

    private EngXExtraMileRating getMockRow(long uid,long engXRating, long extraMileRating) {
        EngXExtraMileRating row = new EngXExtraMileRating();
        row.setUid(uid);
        row.setEngXRating(engXRating);
        row.setExtraMileRating(extraMileRating);
        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName("STEP_HEROES_2024_V1.xlsx");
        when(topTalentExcelVersionService.saveVersion(anyString(), any(CustomUserPrincipal.class), anyString(), anyString())).thenReturn(version);
        return row;
    }
}
