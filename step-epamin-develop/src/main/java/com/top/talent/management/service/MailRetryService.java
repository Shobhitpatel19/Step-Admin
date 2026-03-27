package com.top.talent.management.service;

import jakarta.mail.internet.MimeMessage;

public interface MailRetryService {
    void sendEmail(String toEmail, MimeMessage message);
    void recover(Exception e, String toEmail,  MimeMessage message);
}
