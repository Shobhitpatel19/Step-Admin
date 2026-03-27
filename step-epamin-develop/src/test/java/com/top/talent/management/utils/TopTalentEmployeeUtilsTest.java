package com.top.talent.management.utils;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.exception.InvalidFileFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.top.talent.management.utils.TopTalentEmployeeUtils.extractVersionName;
import static com.top.talent.management.utils.TopTalentEmployeeUtils.extractYear;
import static com.top.talent.management.utils.TopTalentEmployeeUtils.validateYear;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TopTalentEmployeeUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"STEP_2023_V1.xlsx", "STEP_2025_V1.xlsx"})
    void extractYear_ValidFile(String fileName) {
        String extractedYear = extractYear(fileName);
        String expectedYear = fileName.split("_")[1];
        assertEquals(expectedYear, extractedYear);
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID_FILE.xlsx", "STEP_V1.xlsx", "STEP_202X_V1.xlsx", "STEP_2024_.xlsx"})
    void extractYear_InvalidFile(String fileName) {
        InvalidFileFormatException exception = assertThrows(InvalidFileFormatException.class, () -> extractYear(fileName));
        assertEquals(ErrorMessages.INVALID_FILE_FORMAT, exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023", "2025"})
    void validateYear_ValidYear(String fileYear) {
        int currentYear = Integer.parseInt(fileYear); // Simulating the same year for testing
        assertTrue(validateYear(fileYear, currentYear));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023", "2025"})
    void validateYear_InvalidYear(String fileYear) {
        int currentYear = 2024;
        assertFalse(validateYear(fileYear, currentYear));
    }

    @Test
    void extractFileName_ValidVersion() {
        String fileName = "STEP_2020_V1.xlsx";
        String extractedName = extractVersionName(fileName);

        assertEquals("V1", extractedName);
    }

    @Test
    void testFileName_ValidFormat() {
        String validFileName = "STEP_2024_V1.xlsx";
        assertTrue(validFileName.matches("STEP_\\d{4}_V\\d+\\.(xlsx|xls)$"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"STEP_2024_V1.xlsm", "STEP_2024_V1.pdf", "STEP_2024_V1", "STEP_2024 _V1.xlsx", "INVALID_FILE_2024_V1.xlsx"})
    void extractYear_InvalidFileNames_ThrowsException(String fileName) {
        InvalidFileFormatException exception = assertThrows(InvalidFileFormatException.class, () -> TopTalentEmployeeUtils.extractYear(fileName));
        assertEquals(ErrorMessages.INVALID_FILE_FORMAT, exception.getMessage());
    }

    @Test
    void testGetPreviousVersion() {
        // Valid cases
        assertThat(TopTalentEmployeeUtils.getPreviousVersion("V2")).isEqualTo("V1");
        assertThat(TopTalentEmployeeUtils.getPreviousVersion("V10")).isEqualTo("V9");

        // Edge case: Version 0
        assertThat(TopTalentEmployeeUtils.getPreviousVersion("V0")).isEqualTo("V-1"); // Negative version case
    }

    @Test
    void testGetPreviousYear() {
        // Valid cases
        assertThat(TopTalentEmployeeUtils.getPreviousYear("2024")).isEqualTo("2023");
        assertThat(TopTalentEmployeeUtils.getPreviousYear("2000")).isEqualTo("1999");
        assertThat(TopTalentEmployeeUtils.getPreviousYear("1")).isEqualTo("0");

        // Edge case: Year 0 (Could go negative)
        assertThat(TopTalentEmployeeUtils.getPreviousYear("0")).isEqualTo("-1");

        // Invalid format case (should throw an exception)
        assertThatThrownBy(() -> TopTalentEmployeeUtils.getPreviousYear("Year2024"))
                .isInstanceOf(NumberFormatException.class);

        assertThatThrownBy(() -> TopTalentEmployeeUtils.getPreviousYear("abcd"))
                .isInstanceOf(NumberFormatException.class);
    }
}
