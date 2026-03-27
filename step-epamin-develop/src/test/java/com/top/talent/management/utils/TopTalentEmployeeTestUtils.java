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

public class TopTalentEmployeeTestUtils {

    public static MockMultipartFile createFile(String fileName, String[] headers) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("John Doe");
            row.createCell(1).setCellValue(123456L);
            row.createCell(2).setCellValue("john@epam.com");
            row.createCell(3).setCellValue("Location");
            row.createCell(4).setCellValue("2021-01-01");
            row.createCell(5).setCellValue("3 years");
            row.createCell(6).setCellValue("Software Engineer");
            row.createCell(7).setCellValue("Active");
            row.createCell(8).setCellValue("Tech");
            row.createCell(9).setCellValue("Development");
            row.createCell(10).setCellValue("ManagerName");
            row.createCell(11).setCellValue("PGM123");
            row.createCell(12).setCellValue("Proj123");
            row.createCell(13).setCellValue("L2");
            row.createCell(14).setCellValue("Java");
            row.createCell(15).setCellValue("Java Developer");
            row.createCell(16).setCellValue("Spring Boot");
            row.createCell(17).setCellValue("Yes");
            row.createCell(18).setCellValue("rising star");
            row.createCell(19).setCellValue("super star");
            row.createCell(20).setCellValue(4.0);
            workbook.write(outputStream);
        }
        return new MockMultipartFile("file", fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", outputStream.toByteArray());
    }


    public static MockMultipartFile createValidFile(String fileName) throws IOException {
        return createFile(fileName, new String[]{"NAME","UID","Email", "Location","DOJ","Time with Epam","TITLE","STATUS","PRODUCTION CATEGORY","JOB FUNCTION"
                ,"RESOURCE MANAGER","PGM","PROJECT CODE","JF_LEVEL","Competency /Practice","Primary Skill","Niche Skills","Niche Skill(Yes/No)",
                "Talent Profile previous year","Talent Profile current year", "Delivery Feedback TT Score"});
    }

    public static TopTalentExcelVersion createVersion(String fileName, String year, String versionName) {
        TopTalentExcelVersion version = new TopTalentExcelVersion();
        version.setFileName(fileName);
        version.setUploadedYear(year);
        version.setVersionName(versionName);
        return version;
    }

    public static TopTalentEmployee createEmployee(Long uid, TopTalentExcelVersion version) {
        return TopTalentEmployee.builder()
                .uid(uid)
                .name("john")
                .email("john@epam.com")
                .topTalentExcelVersion(version)
                .location("Location")
                .doj("2021-01-01")
                .timeWithEPAM("3 years")
                .title("Software Engineer")
                .status("Active")
                .productionCategory("Tech")
                .jobFunction("Development")
                .resourceManager("ManagerName")
                .pgm("PGM123")
                .projectCode("Proj123")
                .jfLevel("L2")
                .competencyPractice("Java")
                .primarySkill("Java Developer")
                .nicheSkills("Spring Boot")
                .nicheSkillYesNo("Yes")
                .talentProfilePreviousYear("rising star")
                .talentProfile("super star")
                .hrbpMapping("HRBP1")
                .dh("DH1")
                .isStepUser(true)
                .deliveryFeedbackTtScore(4.0)
                .build();
    }
}
