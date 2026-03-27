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

public class EngXExtraMileTestUtils {

    public static MockMultipartFile createFile(String fileName, String[] headers) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(123456L);
            row.createCell(1).setCellValue(4L);
            row.createCell(2).setCellValue(4L);
            workbook.write(outputStream);
        }
        return new MockMultipartFile("file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", outputStream.toByteArray());
    }

    public static MockMultipartFile createEmptyFile(String fileName) {
        return new MockMultipartFile("file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
    }

    public static MockMultipartFile createValidFile(String fileName) throws IOException {
        return createFile(fileName, new String[]{"UID", "Rating Score for EngX", "Rating for Extra Mile"});
    }

    public static MockMultipartFile createFileWithInvalidHeaders(String fileName) throws IOException {
        return createFile(fileName, new String[]{"UID2", "Rating Score for EngX", "Rating Extra Mile"});
    }

    public static TopTalentExcelVersion createVersion(String fileName, String year, String versionName) {
        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName(fileName);
        version.setUploadedYear(year);
        version.setVersionName(versionName);
        return version;
    }

    public static TopTalentEmployee createEmployee(Long uid, Long engX, Long extra) {
        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setUid(uid);
        employee.setContributionEngXCulture(engX);
        employee.setContributionExtraMiles(extra);
        return employee;
    }


}
