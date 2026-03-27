package com.top.talent.management.utils;

import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CulturalScoreTestUtils {

    // Creates an Excel file with specified headers and sample data
    public static MockMultipartFile createFile(String fileName, String[] headers) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(1L);  // Sample UID
            row.createCell(1).setCellValue(4L);  // Sample rating score for Cultural Score
            row.createCell(2).setCellValue(4L);  // Sample rating for Extra Mile
            workbook.write(outputStream);
        }
        return new MockMultipartFile("file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", outputStream.toByteArray());
    }

    // Creates an empty Excel file
    public static MockMultipartFile createEmptyFile(String fileName) {
        return new MockMultipartFile("file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
    }

    // Creates a valid Excel file with Cultural Score headers
    public static MockMultipartFile createValidFile(String fileName) throws IOException {
        return createFile(fileName, new String[]{"UID", "Rating Score for Cultural Score", "Rating for Extra Mile"});
    }

    // Creates an Excel file with invalid headers
    public static MockMultipartFile createFileWithInvalidHeaders(String fileName) throws IOException {
        return createFile(fileName, new String[]{"UID2", "Invalid Cultural Score", "Rating Extra Mile"});
    }

    // Creates a TopTalentExcelVersion object with the given details
    public static TopTalentExcelVersion createVersion(String fileName, String year, String versionName) {
        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName(fileName);
        version.setUploadedYear(year);
        version.setVersionName(versionName);
        return version;
    }

    // Creates a TopTalentEmployee object with sample UID and ratings for Cultural Score and Extra Mile
    public static TopTalentEmployee createEmployee(Long uid, Double culturalScore, Long extraMileScore) {
        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setUid(uid);
        employee.setCultureScoreFromFeedback(culturalScore); // Assuming this field exists in TopTalentEmployee
        employee.setContributionExtraMiles(extraMileScore);
        return employee;
    }

}
