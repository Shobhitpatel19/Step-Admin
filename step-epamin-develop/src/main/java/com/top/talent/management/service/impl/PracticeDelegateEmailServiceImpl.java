package com.top.talent.management.service.impl;

import com.top.talent.management.entity.Delegation;
import com.top.talent.management.entity.PracticeDelegationFeature;
import com.top.talent.management.exception.EmailException;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.PracticeDelegateEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableAsync
@RequiredArgsConstructor
public class PracticeDelegateEmailServiceImpl implements PracticeDelegateEmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.sender-address}")
    private String fromEmail;

    @Async("practiceDelegateEmailExecutor")
    @Override
    public void sendNotificationMailToDelegate(Delegation delegation,  String toName, String toEmail, String type) {
        try {
            MimeMessage message = generateEmailHtml(delegation,toName, toEmail, type);
            log.info("Sending email to delegate...");
            mailSender.send(message);
            log.info("Email sent successfully to delegate");
        } catch (MessagingException | MailException e) {
            log.debug("{}", e.getMessage());
            throw new EmailException(e.getLocalizedMessage());
        }
    }

    private MimeMessage generateEmailHtml(Delegation delegation, String toName, String toEmail, String type) throws MessagingException {
        CustomUserPrincipal userPrincipal = CustomUserPrincipal.getLoggedInUser();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Determine subject and template based on type
        String subject;
        String templateName;

        switch (type.toLowerCase()) {
            case "create":
                subject = "You've Been Assigned as a Delegate in the Top Talent Portal";
                templateName = "PracticeDelegateNotification";
                break;
            case "update":
                subject = "Your Delegate Role Has Been Updated in the Top Talent Portal";
                templateName = "PracticeDelegateUpdatedNotification";
                break;
            case "delete":
                subject = "Your Delegate Role Has Been Removed from the Top Talent Portal";
                templateName = "PracticeDelegateDeletedNotification";
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setFrom(fromEmail);
        helper.setCc(userPrincipal.getEmail());
        Context context = new Context();
        if(delegation==null){
            context.setVariables(
                    Map.of(
                            "name", toName,
                            "practiceHeadName", userPrincipal.getFullName(),
                            "year", LocalDateTime.now().getYear()
                    )
            );
        }
        else {
            String accessType;
            if (delegation.getApprovalRequired()) {
                accessType = "Limited access (approval required)";
            } else {
                accessType = "Full access (no approval required)";
            }

            context.setVariables(
                    Map.of(
                            "name", toName,
                            "practiceHeadName", userPrincipal.getFullName(),
                            "year", LocalDateTime.now().getYear(),
                            "delegationFeatures", delegation.getPracticeDelegationFeatures()
                                    .stream().map(PracticeDelegationFeature::getName)
                                    .collect(Collectors.joining(", ")),
                            "approvalRequired", accessType
                    )
            );
        }
        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);



        return message;
    }



}
