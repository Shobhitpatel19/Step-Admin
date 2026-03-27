package com.top.talent.management.service;

import com.top.talent.management.entity.Delegation;
import org.springframework.stereotype.Service;

@Service
public interface PracticeDelegateEmailService {
    void sendNotificationMailToDelegate(Delegation delegation,String toName, String toEmail, String type);
}
