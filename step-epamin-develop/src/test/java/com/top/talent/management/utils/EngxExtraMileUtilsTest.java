package com.top.talent.management.utils;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.exception.EngXExtraMileRatingException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EngxExtraMileUtilsTest {

    @Test
    void testExtractYear() {

        String fileName = "STEP_HEROES_2024_V1.xlsx";

        String extractedYear = EngxExtraMileUtils.extractYear(fileName);

        assertEquals("2024", extractedYear, "The extracted year should match the expected value.");
    }

    @Test
    void testExtractVersionName() {

        String fileName = "STEP_HEROES_2024_V1.xlsx";

        String versionName = EngxExtraMileUtils.extractVersionName(fileName);

        assertEquals("V1", versionName, "The extracted version name should match the expected value.");
    }

    @Test
    void testValidateYear_ValidYear() {

        String fileYear = String.valueOf(LocalDateTime.now().getYear());

        Boolean isValid = EngxExtraMileUtils.validateYear(fileYear);

        assertTrue(isValid, "The validation should return true for the current year.");
    }

    @Test
    void testValidateYear_InvalidYear() {

        String fileYear = "2020";

        Boolean isValid = EngxExtraMileUtils.validateYear(fileYear);

        assertFalse(isValid, "The validation should return false for a non-current year.");
    }


    @Test
    void testExtractVersionName_InvalidFileName() {

        String fileName = "file_extraMile_2023";

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            EngxExtraMileUtils.extractVersionName(fileName);
        }, "An invalid file name should throw ArrayIndexOutOfBoundsException.");
    }

    @Test
    void testExtractYear_ValidFileName() {
        String fileName = "STEP_HEROES_2023_V2.xlsx";

        String extractedYear = EngxExtraMileUtils.extractYear(fileName);

        assertEquals("2023", extractedYear, "The extracted year should match the expected value.");
    }

    @Test
    void testExtractYear_InvalidFileName_Format() {
        String fileName = "BONUS_2023.xlsx";

        Exception exception = assertThrows(EngXExtraMileRatingException.class, () -> {
            EngxExtraMileUtils.extractYear(fileName);
        });

        assertEquals(ErrorMessages.INVALID_FILE_FORMAT, exception.getMessage(), "Should throw an exception for an invalid file format.");
    }

    @Test
    void testExtractYear_NullFileName() {
        String fileName = null;

        Exception exception = assertThrows(EngXExtraMileRatingException.class, () -> {
            EngxExtraMileUtils.extractYear(fileName);
        });

        assertEquals(ErrorMessages.INVALID_FILE_FORMAT, exception.getMessage(), "Should throw an exception for a null filename.");
    }
}
