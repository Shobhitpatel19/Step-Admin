package com.top.talent.management.service;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.Delegation;
import com.top.talent.management.exception.EmailException;
import com.top.talent.management.service.impl.PracticeDelegateEmailServiceImpl;
import com.top.talent.management.utils.TestUtils;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PracticeDelegateEmailServiceImplTest {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private TemplateEngine templateEngine;
    @InjectMocks
    private PracticeDelegateEmailServiceImpl practiceDelegateEmailService;

    public PracticeDelegateEmailServiceImplTest() {
        TestUtils.getMockAuthenticationWithSecurity(RoleConstants.ROLE_PRACTICE);
    }

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(practiceDelegateEmailService, "fromEmail", "test@email.com");
    }

    @Test
    void testSendNotificationMailToDelegate_Success() {
        MimeMessage message = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("htmlContent");
        doNothing().when(mailSender).send(message);
        Delegation delegation = mock(Delegation.class);
        practiceDelegateEmailService.sendNotificationMailToDelegate(delegation,"to", "to" ,"create");
        verify(mailSender).send(message);
    }

    @Test
    void testSendNotificationMailToDelegate_Exception() {
        MimeMessage message = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("htmlContent");

        doThrow(new MailSendException("Mail send exception")).doNothing().when(mailSender).send(message);
        Delegation delegation = mock(Delegation.class);
        assertThrows(EmailException.class, () ->
                practiceDelegateEmailService.sendNotificationMailToDelegate(delegation,"to", "to","update"));
        verify(mailSender).send(message);
    }

}
