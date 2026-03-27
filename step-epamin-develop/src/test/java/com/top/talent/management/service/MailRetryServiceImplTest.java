package com.top.talent.management.service;
import com.top.talent.management.exception.EmailException;
import com.top.talent.management.service.impl.MailRetryServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class MailRetryServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Autowired
    private MailRetryServiceImpl mailRetryService;

    @BeforeEach
    void setUp() {
        javaMailSender = mock(JavaMailSender.class);
        mailRetryService = new MailRetryServiceImpl(javaMailSender);
    }

    @Test
    void sendEmail_Success() {

        MimeMessage mockMessage = mock(MimeMessage.class);
        mailRetryService.sendEmail("test@example.com", mockMessage);
        verify(javaMailSender, times(1)).send(mockMessage);
    }

    @Test
    void recover_HandlesFailureGracefully() {

        String toEmail = "test@example.com";

        Exception exception = new MessagingException("Messaging error");
        MimeMessage message = mock(MimeMessage.class);

        EmailException thrownException = assertThrows(EmailException.class, () ->
                mailRetryService.recover(exception, toEmail, message ));
        assert(thrownException.getMessage().contains(toEmail));
    }
}
