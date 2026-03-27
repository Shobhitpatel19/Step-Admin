package com.top.talent.management.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.EmptyFileException;
import com.top.talent.management.exception.InvalidCandidateException;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.TopTalentExcelVersionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.AbstractMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

 class ExcelRowListenerTest {

    private ExcelRowListener listener;

    @Mock
    private CustomUserPrincipal customUserPrincipal;

    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;

    @Mock
    private AnalysisContext analysisContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new ExcelRowListener(topTalentExcelVersionService,"STEP_2024_V1.xlsx", customUserPrincipal);
    }

    @Test
    void testValidateMandatoryFields_missingUid() {
        TopTalentEmployee employee = getMockRowWithNullUid();

        InvalidCandidateException exception = assertThrows(InvalidCandidateException.class, () -> {
           listener.validateMandatoryFields(employee);
        });
        assertEquals(ErrorMessages.NULL_UID, exception.getMessage());
    }

    @Test
    void testValidateMandatoryFields_invalidUidFormat() {
        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setUid(123L);
        InvalidCandidateException exception = assertThrows(InvalidCandidateException.class, () -> {
            listener.validateMandatoryFields(employee);
        });
        assertEquals(ErrorMessages.INVALID_UID, exception.getMessage());
    }

    @Test
    void testValidateMandatoryFields_missingDOJ() {
        TopTalentEmployee employee = getMockRow();
        employee.setUid(123456L); // Valid UID

        listener.validateMandatoryFields(employee);

        assertTrue(listener.getErrorMessages().contains(ErrorMessages.MISSING_DOJ+" for UID: 123456"));
    }

    @Test
    void testDoAfterAllAnalysed_EmptyFile() {
        // Given: An empty topTalentEmployeeList
        listener.getTopTalentEmployeeList().clear();  // Ensure the list is empty

        // When & Then: Expect an EmptyFileException to be thrown
        EmptyFileException exception = assertThrows(EmptyFileException.class, () -> listener.doAfterAllAnalysed(analysisContext));

        // Then: Verify the exception message
        assertEquals(ErrorMessages.EMPTY_FILE, exception.getMessage());
    }


    @Test
    void testInvokeHeadMap_missingRequiredColumns() {
        // Given: A header map missing some required columns
        Map<Integer, String> headMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(0, "NAME"), // Present
                new AbstractMap.SimpleEntry<>(1, "UID"),  // Present
                new AbstractMap.SimpleEntry<>(2, "Location") // Present
                // Missing several required columns like "DOJ", "Time with Epam", etc.
        );

        // When & Then: Invoke header validation and assert that InvalidCandidateException is thrown
        InvalidCandidateException exception = assertThrows(InvalidCandidateException.class, () -> {
            listener.invokeHeadMap(headMap, analysisContext);
        });

        // Then: Verify the error message contains the appropriate invalid header message
        assertEquals("Invalid header, please make sure that the file has the correct headers", exception.getMessage());
    }

    @Test
    void testInvokeHeadMap_validHeader() {
        // Given: A valid header map with all required columns
        Map<Integer, String> headMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(0, "NAME"),
                new AbstractMap.SimpleEntry<>(1, "UID"),
                new AbstractMap.SimpleEntry<>(2, "Email"),
                new AbstractMap.SimpleEntry<>(3, "Location"),
                new AbstractMap.SimpleEntry<>(4, "DOJ"),
                new AbstractMap.SimpleEntry<>(5, "Time with Epam"),
                new AbstractMap.SimpleEntry<>(6, "TITLE"),
                new AbstractMap.SimpleEntry<>(7, "STATUS"),
                new AbstractMap.SimpleEntry<>(8, "PRODUCTION CATEGORY"),
                new AbstractMap.SimpleEntry<>(9, "JOB FUNCTION"),
                new AbstractMap.SimpleEntry<>(10, "RESOURCE MANAGER"),
                new AbstractMap.SimpleEntry<>(12, "PGM"),
                new AbstractMap.SimpleEntry<>(13, "PROJECT CODE"),
                new AbstractMap.SimpleEntry<>(14, "JF_LEVEL"),
                new AbstractMap.SimpleEntry<>(15, "Competency /Practice"),
                new AbstractMap.SimpleEntry<>(16, "Primary Skill"),
                new AbstractMap.SimpleEntry<>(17, "Niche Skills"),
                new AbstractMap.SimpleEntry<>(18, "Niche Skill(Yes/No)"),
                new AbstractMap.SimpleEntry<>(19, "Talent Profile previous year"),
                new AbstractMap.SimpleEntry<>(20, "Talent Profile current year"),
                new AbstractMap.SimpleEntry<>(21, "Delivery Feedback TT Score")
        );

        // When & Then: Invoke header validation and assert that no exception is thrown
        assertDoesNotThrow(() -> {
            listener.invokeHeadMap(headMap, analysisContext);
        });
    }

    @Test
    void testInvoke_DuplicateUID() {
        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName("STEP_2024_V1.xlsx");
        when(topTalentExcelVersionService.saveVersion(anyString(), any(CustomUserPrincipal.class), anyString(), anyString())).thenReturn(version);

        TopTalentEmployee firstRow = getMockRow();
        listener.invoke(firstRow, analysisContext);

        TopTalentEmployee row = new TopTalentEmployee();
        row.setUid(123456L);

        InvalidCandidateException exception = assertThrows(InvalidCandidateException.class, () -> listener.invoke(row, analysisContext));
        assertEquals(ErrorMessages.DUPLICATE_UID + "123456", exception.getMessage());
    }

    @Test
    void testInvoke_NullUID() {
        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName("STEP_2024_V1.xlsx");
        when(topTalentExcelVersionService.saveVersion(anyString(), any(CustomUserPrincipal.class), anyString(), anyString())).thenReturn(version);

        TopTalentEmployee row = getMockRowWithNullUid();

        InvalidCandidateException exception = assertThrows(InvalidCandidateException.class, () -> listener.invoke(row, analysisContext));
        assertEquals(ErrorMessages.NULL_UID, exception.getMessage());
    }

    private TopTalentEmployee getMockRowWithNullUid() {
        TopTalentEmployee row = new TopTalentEmployee();
        row.setUid(null);  // Setting UID to null
        return row;
    }
    private TopTalentEmployee getMockRow() {
        TopTalentEmployee row = new TopTalentEmployee();
        row.setUid(123456L);
        return row;
    }
}
