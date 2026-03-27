package com.top.talent.management.service.impl;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.exception.EmailException;
import com.top.talent.management.service.MailRetryService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailRetryServiceImpl implements MailRetryService {

    private final JavaMailSender javaMailSender;

    @Retryable(
            retryFor = {MailAuthenticationException.class, MessagingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Async("asyncEmailNotificationExecutor")
    public void sendEmail(String toEmail,  MimeMessage message){

        log.info("Attempting to send email to {} ", toEmail);
        javaMailSender.send(message);
        log.info("Email successfully sent to {}", toEmail);
    }

    @Recover
    public void recover(Exception e, String toEmail,  MimeMessage message) {
        log.error("Email could not be sent to {} after multiple attempts", toEmail);
        log.info("ERROR IS : " + e.getMessage());
        throw new EmailException(ErrorMessages.MAIL_NOT_SENT + toEmail);
    }

}
