package com.top.talent.management.service.impl;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.EmailPracticeEmployeeDTO;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.EmailException;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.service.MailGenerationService;
import com.top.talent.management.service.MailRetryService;
import com.top.talent.management.service.MeanRatingService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class MailGenerationServiceImpl implements MailGenerationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MailRetryService mailRetryService;
    private final UserRepository userRepository;
    private final MeanRatingService meanRatingService;
    private final TopTalentEmployeeMapper topTalentEmployeeMapper;
    private final RoleRepository roleRepository;

    @Value("${host_email}")
    private String fromEmail ;

    public void generatePracticeRemainderMailAndSend(String toEmail,
                                                     String subject,
                                                     String fileName,
                                                     String name,
                                                     List<TopTalentEmployee>
                                                             practiceEmployees,
                                                     TopTalentExcelVersion
                                                             topTalentExcelVersion,
                                                     String practiceName)  {

        Role superAdminRole = roleRepository.findByName(RoleConstants.SUPER_ADMIN);

        Role hrbpRole = roleRepository.findByName(RoleConstants.HRBP);

        String[] ccEmails = Stream.concat(
                        userRepository.findAllByRoleAndStatus(superAdminRole, Constants.USER_STATUS_ACTIVE)
                                .stream()
                                .map(User::getEmail),

                        userRepository.findAllByRoleAndStatusAndPractice(hrbpRole, Constants.USER_STATUS_ACTIVE, practiceName)
                                .stream()
                                .map(User::getEmail))

                .toArray(String[]::new);

        try
        {
            if(isValidEmail(toEmail)) {
                Context context = new Context();
                context.setVariable("name", name);
                context.setVariable("year", LocalDate.now().getYear());

                if (!practiceEmployees.isEmpty()) {
                    List<EmailPracticeEmployeeDTO> employeeDTOList = practiceEmployees.stream()
                            .map(topTalentEmployee ->
                                    topTalentEmployeeMapper.toEmailPracticeEmployee(
                                            topTalentEmployee,
                                            meanRatingService.getSubmissionStatus(topTalentEmployee.getUid(), topTalentExcelVersion)
                                    ))
                            .filter(emailPracticeEmployeeDTO -> emailPracticeEmployeeDTO.getStatus() != SubmissionStatus.A)
                            .toList();

                    context.setVariable("names", employeeDTOList);
                }

                String htmlContent = templateEngine.process(fileName, context);
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setCc(ccEmails);
                helper.setText(htmlContent, true);
                mailRetryService.sendEmail(toEmail, message);
            }
            else {
                log.error(ErrorMessages.INVALID_EMAIL, toEmail);
            }
        }
        catch (Exception e)
        {
            log.error(ErrorMessages.MAIL_NOT_GENERATED , toEmail, e);
            throw new EmailException(ErrorMessages.MAIL_NOT_GENERATED + toEmail);
        }
    }

    public void generatePracticeRemainderMailAndSend(String toEmail, String subject, String fileName, String name)  {
        try
        {
            if(isValidEmail(toEmail)){
                Context context = new Context();
                context.setVariable("name", name);
                context.setVariable("year", LocalDate.now().getYear());

                String htmlContent = templateEngine.process(fileName , context);
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                mailRetryService.sendEmail(toEmail, message);
            }
            else{
                log.error(ErrorMessages.INVALID_EMAIL , toEmail);
            }
        }
        catch (Exception e)
        {
            log.error(ErrorMessages.MAIL_NOT_GENERATED , toEmail, e);
        }
    }

    public void generateAdminMailAndSend(String toEmail, String subject, String templateFilename, String adminName, String submittedByName, String practiceName) {
        try {
            if (isValidEmail(toEmail)) {
                Context context = new Context();
                context.setVariable("adminName", adminName);
                context.setVariable("submittedByName", submittedByName);
                context.setVariable("practiceName", practiceName);
                context.setVariable("year", LocalDate.now().getYear());

                String htmlContent = templateEngine.process(templateFilename, context);
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                mailRetryService.sendEmail(toEmail, message);
                log.info("Email successfully sent to Admin: {}", toEmail);
            } else {
                log.error("Invalid email format for Admin: {}", toEmail);
            }
        } catch (Exception e) {
            log.error("Failed to send email to Admin: {}, Error: {}", toEmail, e.getMessage());
            throw new EmailException(ErrorMessages.MAIL_NOT_GENERATED + toEmail);
        }
    }

    public void generateReminderMailAndSend(String toEmail,
                                            String subject,
                                            String templateFilename,
                                            String recipientName) {
        try {
            if (isValidEmail(toEmail)) {
                Context context = new Context();
                context.setVariable("recipientName", recipientName);
                context.setVariable("year", LocalDate.now().getYear());

                String htmlContent = templateEngine.process(templateFilename, context);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(fromEmail);
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                mailRetryService.sendEmail(toEmail, message);
                log.info("Reminder email successfully sent to: {}", toEmail);
            } else {
                log.error("Invalid email format for recipient: {}", toEmail);
            }
        } catch (Exception e) {
            log.error("Failed to send reminder email to: {}, Error: {}", toEmail, e.getMessage());
            throw new EmailException(ErrorMessages.MAIL_NOT_GENERATED + toEmail);
        }
    }
    private boolean isValidEmail(String email) {
        return Pattern.matches(Constants.EPAM_EMAIL_REGEX, email);
    }
}