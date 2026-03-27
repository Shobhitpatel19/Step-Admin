package com.top.talent.management.service;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.CultureScoreException;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.CulturalScoreServiceImpl;
import com.top.talent.management.service.impl.TopTalentEmployeeServiceImpl;
import com.top.talent.management.service.impl.TopTalentExcelVersionServiceImpl;
import com.top.talent.management.utils.CultureScoreUtils;
import com.top.talent.management.utils.TestUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CulturalScoreServiceImplTest {


    @Mock
    private TopTalentExcelVersionService excelVersionService;

    @Mock
    private TopTalentEmployeeServiceImpl topTalentEmployeeService;

    @Mock
    private TopTalentEmployeeMapper dataMapper;

    @InjectMocks
    private CulturalScoreServiceImpl culturalScoreService;

    @Mock
    private CultureScoreUtils cultureScoreUtils;

    @Mock
    private TopTalentExcelVersionServiceImpl topTalentExcelVersionService;

    @Test
    void parseAndSaveCulturalScore_validFile_success() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String fileName = "STEP_CULTURAL_SCORE_"+year+"_V1.xlsx";

        // Mock Excel file with a single row
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("UID");
            header.createCell(1).setCellValue("Culture Score (from feedback)");
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(178908L);
            row.createCell(1).setCellValue(4L); // Valid Culture Score
            workbook.write(outputStream);
        }

        MockMultipartFile file = new MockMultipartFile(
                "file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray());


        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName(fileName);
        version.setUploadedYear(year);
        version.setVersionName("V1");

        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setUid(178908L);
        employee.setCultureScoreFromFeedback(null); // Initially null
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();

        when(topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, "V1"))
                .thenReturn(Collections.singletonList(employee));
        List<?> result = culturalScoreService.parseAndSaveCulturalScore(file,customUserPrincipal);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4L, employee.getCultureScoreFromFeedback()); // Verify score is updated

        verify(topTalentEmployeeService, times(1)).saveAll(anyList());
    }

    @Test
    void parseAndSaveCulturalScore_validFile_success_IdPhaseClosed() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String fileName = "STEP_CULTURAL_SCORE_"+year+"_V1.xlsx";

        // Mock Excel file with a single row
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("UID");
            header.createCell(1).setCellValue("Culture Score (from feedback)");
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(178908L);
            row.createCell(1).setCellValue(4L); // Valid Culture Score
            workbook.write(outputStream);
        }

        MockMultipartFile file = new MockMultipartFile(
                "file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray());


        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName(fileName);
        version.setUploadedYear(year);
        version.setVersionName("V1");

        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setUid(178908L);
        employee.setCultureScoreFromFeedback(null); // Initially null
        employee.setIsStepUser(true);
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();



        when(topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, "V1"))
                .thenReturn(Collections.singletonList(employee));
        List<?> result = culturalScoreService.parseAndSaveCulturalScore(file,customUserPrincipal);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4L, employee.getCultureScoreFromFeedback()); // Verify score is updated

        verify(topTalentEmployeeService, times(1)).saveAll(anyList());
    }
    @Test
    void parseAndSaveCulturalScore_validFile_success_NOphase() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String fileName = "STEP_CULTURAL_SCORE_"+year+"_V1.xlsx";

        // Mock Excel file with a single row
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("UID");
            header.createCell(1).setCellValue("Culture Score (from feedback)");
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(178908L);
            row.createCell(1).setCellValue(4L); // Valid Culture Score
            workbook.write(outputStream);
        }

        MockMultipartFile file = new MockMultipartFile(
                "file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray());


        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName(fileName);
        version.setUploadedYear(year);
        version.setVersionName("V1");

        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setUid(178908L);
        employee.setCultureScoreFromFeedback(null); // Initially null
        employee.setIsStepUser(true);
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();

        when(topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, "V1"))
                .thenReturn(Collections.singletonList(employee));
        List<?> result = culturalScoreService.parseAndSaveCulturalScore(file,customUserPrincipal);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4L, employee.getCultureScoreFromFeedback()); // Verify score is updated

        verify(topTalentEmployeeService, times(1)).saveAll(anyList());
    }
    @Test
    void parseAndSaveCulturalScore_invalidFileName_throwsException() {
        String fileName = "INVALID_FILE_NAME.xlsx";
        MockMultipartFile file = new MockMultipartFile(
                "file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();
        Exception exception = assertThrows(RuntimeException.class,
                () -> culturalScoreService.parseAndSaveCulturalScore(file, customUserPrincipal));

        assertEquals(ErrorMessages.INVALID_FILE_FORMAT, exception.getMessage());
    }

    @Test
    void parseAndSaveCulturalScore_mismatchedSizes_throwsException() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String fileName = "STEP_CULTURAL_SCORE_"+year+"_V1.xlsx";

        // Mock Excel with one record
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("UID");
            header.createCell(1).setCellValue("Culture Score (from feedback)");
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(1L); // Mock UID
            row.createCell(1).setCellValue(5L); // Mock culture score
            workbook.write(outputStream);
        }

        MockMultipartFile file = new MockMultipartFile(
                "file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                outputStream.toByteArray());

        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();


        when(topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, "V1"))
                .thenReturn(Collections.emptyList());

        // Act and Assert
        Exception exception = assertThrows(CultureScoreException.class,
                () -> culturalScoreService.parseAndSaveCulturalScore(file,customUserPrincipal));

        // Validate exception
        assertEquals(CultureScoreException.class, exception.getClass());
        assertEquals(ErrorMessages.LIST_NOT_FOUND, exception.getMessage());

    }


    @Test
    void parseAndSaveCulturalScore_emptyEmployeeList_throwsEmptyFileException() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String fileName = "STEP_CULTURAL_SCORE_"+year+"_V1.xlsx";

        MockMultipartFile file = new MockMultipartFile("file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);


        when(topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, "V1"))
                .thenReturn(Collections.emptyList());
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();
        Exception exception = assertThrows(CultureScoreException.class, () ->
                culturalScoreService.parseAndSaveCulturalScore(file, customUserPrincipal));

        assertEquals(ErrorMessages.LIST_NOT_FOUND, exception.getMessage());
    }


}
