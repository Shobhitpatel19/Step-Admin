package com.top.talent.management.service;

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
import com.top.talent.management.service.impl.MailGenerationServiceImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

class MailGenerationServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MailRetryService mailRetryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MeanRatingService meanRatingService;

    @Mock
    private TopTalentEmployeeMapper topTalentEmployeeMapper;

    @InjectMocks
    private MailGenerationServiceImpl mailGenerationService;

    @Mock
    private TopTalentExcelVersion topTalentExcelVersion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGeneratePracticeRemainderMailAndSend_ThrowsEmailException() {
        String toEmail = "user@epam.com";
        String subject = "Test Subject";
        String fileName = "emailTemplate";
        String name = "Test Name";
        String practice = "Practice";
        List<TopTalentEmployee> practiceEmployees = List.of(new TopTalentEmployee());


        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mock Exception"));

        EmailException exception = assertThrows(EmailException.class, () ->
                mailGenerationService.generatePracticeRemainderMailAndSend(toEmail, subject, fileName, name, practiceEmployees, topTalentExcelVersion,practice)
        );

        assertEquals(ErrorMessages.MAIL_NOT_GENERATED + toEmail, exception.getMessage());
    }

    @Test
    void testGenerateEmailAndSend_WithoutNamesList() {

        ReflectionTestUtils.setField(mailGenerationService, "fromEmail", "step@epam.com");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("template.html"), any(Context.class))).thenReturn("<html>Test Content</html>");

        mailGenerationService.generatePracticeRemainderMailAndSend("user@epam.com", "Test Subject", "template.html", "Test User");

        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("template.html"), any(Context.class));
    }


    @Test
    void testGeneratePracticeRemainderMailAndSend_WithList()  {
        Role superAdminRole = new Role();
        superAdminRole.setId(1L);
        List<User> usersList = List.of(User.builder().email("EMAIL1@epam.com").build());

        when(roleRepository.findByName(RoleConstants.SUPER_ADMIN)).thenReturn(superAdminRole);
        when(userRepository.findAllByRoleAndStatus(superAdminRole, Constants.USER_STATUS_ACTIVE)).thenReturn(usersList);

        ReflectionTestUtils.setField(mailGenerationService, "fromEmail", "step@epam.com");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("template.html"), any(Context.class))).thenReturn("<html>Test Content</html>");

        TopTalentExcelVersion excelVersion = new TopTalentExcelVersion();
        excelVersion.setId(1L);
        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setUid(12L);
        employee.setTopTalentExcelVersion(excelVersion);
        List<TopTalentEmployee> practiceEmployees = List.of(employee);
        when(meanRatingService.getSubmissionStatus(12L,excelVersion)).thenReturn(SubmissionStatus.S);
        EmailPracticeEmployeeDTO practiceEmployeeDTO=new EmailPracticeEmployeeDTO(12L,null,SubmissionStatus.S);
        when(topTalentEmployeeMapper.toEmailPracticeEmployee(employee,SubmissionStatus.S)).thenReturn(practiceEmployeeDTO);

        mailGenerationService.generatePracticeRemainderMailAndSend("user@epam.com", "Test Subject", "template.html", "Test User", practiceEmployees, excelVersion, "Practice");

        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("template.html"), any(Context.class));
    }

    @Test
    void testGenerateEmailAndSendWithNames_EmailGenerationFailure() {
        String toEmail = "user@epam.com";
        String subject = "Test Subject";
        String fileName = "template.html";
        String name = "Test User";
        String practice = "Practice";
        List<TopTalentEmployee> names = List.of(new TopTalentEmployee());

        when(templateEngine.process(eq(fileName), any(Context.class))).thenThrow(new RuntimeException("Template processing failed"));

        EmailException exception = assertThrows(
                EmailException.class,
                () -> mailGenerationService.generatePracticeRemainderMailAndSend(toEmail, subject, fileName, name, names, topTalentExcelVersion, practice)
        );
        assertEquals(ErrorMessages.MAIL_NOT_GENERATED + toEmail, exception.getMessage());
    }

    @Test
    void testGenerateAdminMailAndSend_whenValidInput_shouldSendEmailSuccessfully() {
        String toEmail = "admin@epam.com";
        String subject = "Future Skills Submission Notification";
        String template = "AdminEmailOnFutureSkills";
        String adminName = "Admin Name";
        String submittedByName = "Practice Head";
        String practiceName = "Engineering Practice";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(template), any(Context.class))).thenReturn("<html>Email Content</html>");

        ReflectionTestUtils.setField(mailGenerationService, "fromEmail", "noreply@epam.com");

        mailGenerationService.generateAdminMailAndSend(toEmail, subject, template, adminName, submittedByName, practiceName);

        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq(template), any(Context.class));
        verify(mailRetryService).sendEmail(eq(toEmail), eq(mimeMessage));
    }

    @Test
    void testGenerateAdminMailAndSend_whenEmailIsInvalid_shouldNotSendEmail() {
        String toEmail = "invalid-email";
        String subject = "Subject";
        String template = "AdminEmailOnFutureSkills";
        String adminName = "Admin";
        String submittedByName = "Submitter";
        String practiceName = "Practice";

        mailGenerationService.generateAdminMailAndSend(toEmail, subject, template, adminName, submittedByName, practiceName);

        verify(mailSender, never()).createMimeMessage();
        verify(mailRetryService, never()).sendEmail(any(), any());
    }

    @Test
    void testGenerateAdminMailAndSend_shouldThrowEmailException() {
        String toEmail = "admin@epam.com";
        String subject = "Admin Subject";
        String template = "AdminEmailOnFutureSkills";
        String adminName = "Admin Name";
        String submittedByName = "Submitter Name";
        String practiceName = "Practice Name";

        ReflectionTestUtils.setField(mailGenerationService, "fromEmail", "noreply@epam.com");
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Simulated failure"));

        EmailException exception = assertThrows(
                EmailException.class,
                () -> mailGenerationService.generateAdminMailAndSend(toEmail, subject, template, adminName, submittedByName, practiceName)
        );

        assertEquals(ErrorMessages.MAIL_NOT_GENERATED + toEmail, exception.getMessage());
        verify(mailSender).createMimeMessage();
        verify(mailRetryService, Mockito.never()).sendEmail(any(), any());
    }

    @Test
    void testGenerateReminderMailAndSend_validEmail_shouldSendEmail() {

        ReflectionTestUtils.setField(mailGenerationService, "fromEmail", "noreply@epam.com");
        String toEmail = "validuser@epam.com";
        String subject = "Reminder Subject";
        String templateFilename = "ReminderTemplate.html";
        String recipientName = "John Doe";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(templateFilename), any(Context.class))).thenReturn("<html>Email Content</html>");

        mailGenerationService.generateReminderMailAndSend(toEmail, subject, templateFilename, recipientName);

        verify(mailSender).createMimeMessage();
        verify(mailRetryService).sendEmail(eq(toEmail), eq(mimeMessage));
        verify(templateEngine).process(eq(templateFilename), any(Context.class));
    }

    @Test
    void testGenerateReminderMailAndSend_invalidEmail_shouldNotSendEmail() {
        String toEmail = "invalid-email";
        String subject = "Reminder Subject";
        String templateFilename = "ReminderTemplate.html";
        String recipientName = "John Doe";

        mailGenerationService.generateReminderMailAndSend(toEmail, subject, templateFilename, recipientName);

        verify(mailSender, never()).createMimeMessage();
        verify(mailRetryService, never()).sendEmail(any(), any());
    }

    @Test
    void testGenerateReminderMailAndSend_emailSendingFailure_shouldThrowEmailException() {
        ReflectionTestUtils.setField(mailGenerationService, "fromEmail", "noreply@epam.com");
        String toEmail = "validuser@epam.com";
        String subject = "Reminder Subject";
        String templateFilename = "ReminderTemplate.html";
        String recipientName = "John Doe";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(templateFilename), any(Context.class))).thenReturn("<html>Email Content</html>");
        doThrow(new RuntimeException("Email sending failed")).when(mailRetryService).sendEmail(eq(toEmail), eq(mimeMessage));

        EmailException exception = assertThrows(
                EmailException.class,
                () -> mailGenerationService.generateReminderMailAndSend(toEmail, subject, templateFilename, recipientName)
        );
        assertEquals(ErrorMessages.MAIL_NOT_GENERATED + toEmail, exception.getMessage());
        verify(mailSender).createMimeMessage();
        verify(mailRetryService).sendEmail(eq(toEmail), eq(mimeMessage));
    }

    @Test
    void testGenerateReminderMailAndSend_templateProcessingFailure_shouldThrowEmailException() {
        ReflectionTestUtils.setField(mailGenerationService, "fromEmail", "noreply@epam.com");
        String toEmail = "validuser@epam.com";
        String subject = "Reminder Subject";
        String templateFilename = "ReminderTemplate.html";
        String recipientName = "John Doe";

        when(templateEngine.process(eq(templateFilename), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing failed"));

        EmailException exception = assertThrows(
                EmailException.class,
                () -> mailGenerationService.generateReminderMailAndSend(toEmail, subject, templateFilename, recipientName)
        );
        assertEquals(ErrorMessages.MAIL_NOT_GENERATED + toEmail, exception.getMessage());
        verify(mailSender, never()).createMimeMessage();
    }

}