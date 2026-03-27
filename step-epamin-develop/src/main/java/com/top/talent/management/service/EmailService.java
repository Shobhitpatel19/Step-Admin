package com.top.talent.management.service;


import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.security.CustomUserPrincipal;

import java.time.LocalDate;


public interface EmailService {

    void generateDatesAndTemplates(LocalDate time, TopTalentExcelVersion topTalentExcelVersion);
    void sendMailToPractices(LocalDate time, TopTalentExcelVersion topTalentExcelVersion);
    void sendMailToAdmin(CustomUserPrincipal userPrincipal, String practiceName);
    void sendReminderEmailsForFebruary(int weekOfMonth, boolean isLateReminder);
}