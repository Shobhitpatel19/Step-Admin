package com.top.talent.management.service;

import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;

import java.util.List;

public interface MailGenerationService {
    void generatePracticeRemainderMailAndSend(String toEmail, String subject, String fileName, String name, List<TopTalentEmployee> practiceEmployees, TopTalentExcelVersion topTalentExcelVersion, String practice);
    void generatePracticeRemainderMailAndSend(String toEmail, String subject, String fileName, String name);
    void generateAdminMailAndSend(String toEmail, String subject, String templateFilename, String adminName, String submittedByName, String practiceName);
    void generateReminderMailAndSend(String toEmail, String subject, String templateFilename, String recipientName);
}
