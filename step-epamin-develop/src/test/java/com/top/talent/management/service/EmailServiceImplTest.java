package com.top.talent.management.service;

import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.EmailSchedule;
import com.top.talent.management.entity.NotificationManagement;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.PracticeEmailScheduleLog;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.EmailException;
import com.top.talent.management.repository.EmailScheduleRepository;
import com.top.talent.management.repository.NotificationManagementRepository;
import com.top.talent.management.repository.PracticeEmailScheduleLogRepository;
import com.top.talent.management.repository.RoleRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.EmailServiceImpl;
import com.top.talent.management.service.impl.MailGenerationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class EmailServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopTalentEmployeeRepository topTalentEmployeeRepository;

    @Mock
    private EmailScheduleRepository emailScheduleRepository;

    @Mock
    private PracticeEmailScheduleLogRepository practiceEmailScheduleLogRepository;

    @Mock
    private MailGenerationServiceImpl mailGenerationServiceImpl;

    @Mock
    private CustomUserPrincipal userPrincipal;

    @Spy
    @InjectMocks
    private EmailServiceImpl emailServiceImpl;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private NotificationManagementRepository notificationManagementRepository;

    private LocalDate testDate;
    private TopTalentExcelVersion testExcelVersion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testDate = LocalDate.now();
        testExcelVersion = new TopTalentExcelVersion();
    }


    @Test
    void testGenerateDatesAndTemplates_Success() {
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        TopTalentExcelVersion excelVersion = new TopTalentExcelVersion();

        emailServiceImpl.generateDatesAndTemplates(startDate, excelVersion);

        ArgumentCaptor<EmailSchedule> captor = ArgumentCaptor.forClass(EmailSchedule.class);
        verify(emailScheduleRepository, times(5)).save(captor.capture());

        List<EmailSchedule> savedSchedules = captor.getAllValues();
        assertEquals(5, savedSchedules.size());
        assertEquals("Candidate List Uploaded for Top Talent Program Review", savedSchedules.get(0).getEmailSubject());
        assertEquals("PracticeEmail1", savedSchedules.get(0).getEmailTemplate());
    }

    @Test
    void testGenerateDatesAndTemplates_ExceptionThrown() {
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        TopTalentExcelVersion excelVersion = new TopTalentExcelVersion();

        doThrow(new RuntimeException("DB Error"))
                .when(emailScheduleRepository).save(any(EmailSchedule.class));

        assertThrows(EmailException.class,
                () -> emailServiceImpl.generateDatesAndTemplates(startDate, excelVersion));
    }

    @Test
    void testSendMailToPracticesWhenNoEmailSchedule() {
        when(emailScheduleRepository.existsByDate(testDate)).thenReturn(false);

        emailServiceImpl.sendMailToPractices(testDate, testExcelVersion);

        verify(emailScheduleRepository, times(1)).existsByDate(testDate);
        verifyNoInteractions(userRepository, topTalentEmployeeRepository, mailGenerationServiceImpl);
    }


    @Test
    void testSendMailToPracticesWhenEmployeesExist() {
        EmailSchedule emailSchedule = new EmailSchedule();
        emailSchedule.setIsFirstDate(false);
        emailSchedule.setEmailSubject("Test Subject");
        emailSchedule.setEmailTemplate("Test Template");

        User practiceHead = new User();
        practiceHead.setEmail("practice@example.com");
        practiceHead.setFirstName("John");
        practiceHead.setLastName("Doe");
        practiceHead.setUuid(1L);
        practiceHead.setPractice("Practice");

        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setName("Employee 1");

        NotificationManagement notification = new NotificationManagement();
        notification.setNotificationsEnabled(true);

        when(emailScheduleRepository.existsByDate(testDate)).thenReturn(true);
        when(emailScheduleRepository.findByDate(testDate)).thenReturn(emailSchedule);
        when(userRepository.findAllByRole(any())).thenReturn(List.of(practiceHead));
        when(topTalentEmployeeRepository.findAllByCompetencyPracticeAndPracticeRatingIsNullAndTopTalentExcelVersion(
                any(), eq(testExcelVersion))).thenReturn(List.of(employee));
        when(practiceEmailScheduleLogRepository.existsByPracticeEmailIdAndTopTalentExcelVersion(anyString(), eq(testExcelVersion)))
                .thenReturn(false);
        when(notificationManagementRepository.findByUserUuidAndCategoryCategoryId(any(), eq(2L)))
                .thenReturn(Optional.of(notification));

        emailServiceImpl.sendMailToPractices(testDate, testExcelVersion);
        verify(mailGenerationServiceImpl, times(1)).generatePracticeRemainderMailAndSend(
                eq("practice@example.com"),
                eq("Test Subject"),
                eq("Test Template"),
                eq("John Doe"),
                anyList(),
                eq(testExcelVersion),
                eq("Practice")
        );
        verify(practiceEmailScheduleLogRepository, times(1)).save(any());
        verify(emailScheduleRepository, times(1)).save(any());
    }


    @Test
    void testSendMailToPracticesWhenEmployeesExistAndPracticeNotExist() {
        EmailSchedule emailSchedule = new EmailSchedule();
        emailSchedule.setIsFirstDate(false);
        emailSchedule.setEmailSubject("Test Subject");
        emailSchedule.setEmailTemplate("Test Template");

        when(emailScheduleRepository.existsByDate(testDate)).thenReturn(true);
        when(emailScheduleRepository.findByDate(testDate)).thenReturn(emailSchedule);
        when(userRepository.findAllByRole(any())).thenReturn(Collections.emptyList());
        when(topTalentEmployeeRepository.findAllByCompetencyPracticeAndPracticeRatingIsNullAndTopTalentExcelVersion(
                any(), eq(testExcelVersion))).thenReturn(Collections.emptyList());
        when(practiceEmailScheduleLogRepository.existsByPracticeEmailIdAndTopTalentExcelVersion(anyString(), eq(testExcelVersion)))
                .thenReturn(false);

        emailServiceImpl.sendMailToPractices(testDate, testExcelVersion);

        verify(mailGenerationServiceImpl, never()).generatePracticeRemainderMailAndSend(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyList(),
                any(TopTalentExcelVersion.class),
                anyString()
        );
        verify(practiceEmailScheduleLogRepository, never()).save(any());
        verify(emailScheduleRepository, never()).save(any());
    }
    @Test
    void testSendMailToPractices_UpdateExistingLog() {
        EmailSchedule emailSchedule = new EmailSchedule();
        emailSchedule.setIsFirstDate(false);
        emailSchedule.setEmailSubject("Test Subject");
        emailSchedule.setEmailTemplate("Test Template");

        User practiceHead = new User();
        practiceHead.setEmail("practice@example.com");
        practiceHead.setFirstName("John");
        practiceHead.setLastName("Doe");
        practiceHead.setUuid(1L);
        practiceHead.setPractice("Practice");

        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setName("Employee 1");

        NotificationManagement notification = new NotificationManagement();
        notification.setNotificationsEnabled(true);

        PracticeEmailScheduleLog existingLog = PracticeEmailScheduleLog.builder()
                .practiceEmailId("practice@example.com")
                .noOfEmailsSent(2L)
                .build();

        when(emailScheduleRepository.existsByDate(testDate)).thenReturn(true);
        when(emailScheduleRepository.findByDate(testDate)).thenReturn(emailSchedule);
        when(userRepository.findAllByRole(any())).thenReturn(List.of(practiceHead));
        when(topTalentEmployeeRepository.findAllByCompetencyPracticeAndPracticeRatingIsNullAndTopTalentExcelVersion(any(), eq(testExcelVersion)))
                .thenReturn(List.of(employee));
        when(notificationManagementRepository.findByUserUuidAndCategoryCategoryId(any(), eq(2L)))
                .thenReturn(Optional.of(notification));
        when(practiceEmailScheduleLogRepository.existsByPracticeEmailIdAndTopTalentExcelVersion(anyString(), eq(testExcelVersion)))
                .thenReturn(true);
        when(practiceEmailScheduleLogRepository.findByPracticeEmailId("practice@example.com"))
                .thenReturn(existingLog);

        emailServiceImpl.sendMailToPractices(testDate, testExcelVersion);

        verify(practiceEmailScheduleLogRepository, times(1)).save(existingLog);
        assertEquals(3L, existingLog.getNoOfEmailsSent());
    }
    @Test
    void testSendMailToPractices_NotificationsDisabled() {
        EmailSchedule emailSchedule = new EmailSchedule();
        emailSchedule.setIsFirstDate(false);
        emailSchedule.setEmailSubject("Test Subject");
        emailSchedule.setEmailTemplate("Test Template");

        User practiceHead = new User();
        practiceHead.setEmail("practice@example.com");
        practiceHead.setFirstName("John");
        practiceHead.setLastName("Doe");
        practiceHead.setUuid(1L);
        practiceHead.setPractice("Practice");

        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setName("Employee 1");

        NotificationManagement notification = new NotificationManagement();
        notification.setNotificationsEnabled(false);

        when(emailScheduleRepository.existsByDate(testDate)).thenReturn(true);
        when(emailScheduleRepository.findByDate(testDate)).thenReturn(emailSchedule);
        when(userRepository.findAllByRole(any())).thenReturn(List.of(practiceHead));
        when(topTalentEmployeeRepository.findAllByCompetencyPracticeAndPracticeRatingIsNullAndTopTalentExcelVersion(any(), eq(testExcelVersion)))
                .thenReturn(List.of(employee));
        when(notificationManagementRepository.findByUserUuidAndCategoryCategoryId(any(), eq(2L)))
                .thenReturn(Optional.of(notification));

        emailServiceImpl.sendMailToPractices(testDate, testExcelVersion);

        verify(mailGenerationServiceImpl, never()).generatePracticeRemainderMailAndSend(
                anyString(), anyString(), anyString(), anyString(), anyList(), any(), anyString()
        );
    }
    @Test
    void testSendMailToPractices_NotificationSettingAbsent() {
        EmailSchedule emailSchedule = new EmailSchedule();
        emailSchedule.setIsFirstDate(false);
        emailSchedule.setEmailSubject("Test Subject");
        emailSchedule.setEmailTemplate("Test Template");

        User practiceHead = new User();
        practiceHead.setEmail("practice@example.com");
        practiceHead.setFirstName("John");
        practiceHead.setLastName("Doe");
        practiceHead.setUuid(1L);
        practiceHead.setPractice("Practice");

        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setName("Employee 1");

        when(emailScheduleRepository.existsByDate(testDate)).thenReturn(true);
        when(emailScheduleRepository.findByDate(testDate)).thenReturn(emailSchedule);
        when(userRepository.findAllByRole(any())).thenReturn(List.of(practiceHead));
        when(topTalentEmployeeRepository.findAllByCompetencyPracticeAndPracticeRatingIsNullAndTopTalentExcelVersion(any(), eq(testExcelVersion)))
                .thenReturn(List.of(employee));
        when(notificationManagementRepository.findByUserUuidAndCategoryCategoryId(any(), eq(2L)))
                .thenReturn(Optional.empty());

        emailServiceImpl.sendMailToPractices(testDate, testExcelVersion);

        verify(mailGenerationServiceImpl, never()).generatePracticeRemainderMailAndSend(
                anyString(), anyString(), anyString(), anyString(), anyList(), any(), anyString()
        );
    }

    @Test
    void testSendMailToAdmin_SuccessfulEmailSending() {
        CustomUserPrincipal mockUserPrincipal = mock(CustomUserPrincipal.class);
        when(mockUserPrincipal.getFullName()).thenReturn("Practice Head");

        Role adminRole = new Role();
        when(roleRepository.findByName(RoleConstants.SUPER_ADMIN)).thenReturn(adminRole);

        User admin1 = new User();
        admin1.setEmail("admin1@example.com");
        admin1.setFirstName("AdminOne");

        User admin2 = new User();
        admin2.setEmail("admin2@example.com");
        admin2.setFirstName("AdminTwo");

        List<User> admins = Arrays.asList(admin1, admin2);
        when(userRepository.findAllByRole(adminRole)).thenReturn(admins);

        doNothing().when(mailGenerationServiceImpl).generateAdminMailAndSend(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

        emailServiceImpl.sendMailToAdmin(mockUserPrincipal, "Engineering Practice");

        verify(mailGenerationServiceImpl).generateAdminMailAndSend(
                "admin1@example.com",
                "Future Skills Submission Notification",
                "AdminEmailOnFutureSkills",
                "AdminOne",
                "Practice Head",
                "Engineering Practice"
        );

        verify(mailGenerationServiceImpl).generateAdminMailAndSend(
                "admin2@example.com",
                "Future Skills Submission Notification",
                "AdminEmailOnFutureSkills",
                "AdminTwo",
                "Practice Head",
                "Engineering Practice"
        );

    }

    @Test
    void testSendMailToAdmin_NoAdminsFound() {
        CustomUserPrincipal mockUserPrincipal = mock(CustomUserPrincipal.class);
        when(mockUserPrincipal.getFullName()).thenReturn("Practice Head");

        Role adminRole = new Role();
        when(roleRepository.findByName(RoleConstants.SUPER_ADMIN)).thenReturn(adminRole);
        when(userRepository.findAllByRole(adminRole)).thenReturn(Collections.emptyList());

        emailServiceImpl.sendMailToAdmin(mockUserPrincipal, "Engineering Practice");

        verify(mailGenerationServiceImpl, never()).generateAdminMailAndSend(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testSendReminderEmailsToPractices_noPracticeHeads() {
        Role practiceRole = Role.builder()
                .name("PRACTICE")
                .build();
        Mockito.when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(practiceRole);
        Mockito.when(userRepository.findAllByRole(practiceRole)).thenReturn(Collections.emptyList());

        emailServiceImpl.sendReminderEmailsToPractices("FirstReminder.html", "Test Subject");

        Mockito.verify(mailGenerationServiceImpl, Mockito.never()).generateReminderMailAndSend(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testSendReminderEmailsToPractices_emailsSentSuccessfully() {
        Role practiceRole = Role.builder()
                .name("PRACTICE")
                .build();
        User user = Mockito.mock(User.class);
        Mockito.when(user.getFirstName()).thenReturn("John");
        Mockito.when(user.getLastName()).thenReturn("Doe");
        Mockito.when(user.getEmail()).thenReturn("john.doe@example.com");
        Mockito.when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(practiceRole);
        Mockito.when(userRepository.findAllByRole(practiceRole)).thenReturn(Collections.singletonList(user));

        emailServiceImpl.sendReminderEmailsToPractices("FirstReminder.html", "Test Subject");

        Mockito.verify(mailGenerationServiceImpl, Mockito.times(1)).generateReminderMailAndSend(
                "john.doe@example.com",
                "Test Subject",
                "FirstReminder.html",
                "John Doe"
        );
    }

    @Test
    void testSendReminderEmailsToPractices_exceptionWhileSendingEmail() {
        Role practiceRole = Role.builder()
                .name("PRACTICE")
                .build();
        User user = Mockito.mock(User.class);
        Mockito.when(user.getFirstName()).thenReturn("John");
        Mockito.when(user.getLastName()).thenReturn("Doe");
        Mockito.when(user.getEmail()).thenReturn("john.doe@example.com");
        Mockito.when(roleRepository.findByName(RoleConstants.PRACTICE)).thenReturn(practiceRole);
        Mockito.when(userRepository.findAllByRole(practiceRole)).thenReturn(Collections.singletonList(user));
        Mockito.doThrow(new RuntimeException("Email sending failed"))
                .when(mailGenerationServiceImpl).generateReminderMailAndSend(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertDoesNotThrow(() -> emailServiceImpl.sendReminderEmailsToPractices("FirstReminder.html", "Test Subject"));
    }

    @Test
    void testSendReminderEmailsForFebruary_ThirdFriday_FourthReminder() {
        LocalDate thirdFridayOfFebruary = LocalDate.of(2024, Month.FEBRUARY, 16);

        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(thirdFridayOfFebruary);

            doNothing().when(emailServiceImpl).sendReminderEmailsToPractices(anyString(), anyString());

            emailServiceImpl.sendReminderEmailsForFebruary(3, false);

            verify(emailServiceImpl, times(1)).sendReminderEmailsToPractices(
                    "FourthReminder.html",
                    "Fourth Reminder: Submit Your Future Skills Form"
            );
        }
    }


    @Test
    void testSendReminderEmailsForFebruary_SecondMonday_SecondReminder() {
        LocalDate secondMondayOfFebruary = LocalDate.of(2024, Month.FEBRUARY, 12); // Second Monday

        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(secondMondayOfFebruary);

            doNothing().when(emailServiceImpl).sendReminderEmailsToPractices(anyString(), anyString());

            emailServiceImpl.sendReminderEmailsForFebruary(2, false);

            verify(emailServiceImpl, times(1)).sendReminderEmailsToPractices(
                    "SecondReminder.html",
                    "Second Reminder: Submit Your Future Skills Form"
            );
        }
    }


    @Test
    void testSendReminderEmailsForFebruary_ThirdMonday_ThirdReminder() {
        LocalDate thirdMondayOfFebruary = LocalDate.of(2024, Month.FEBRUARY, 19); // Third Monday

        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(thirdMondayOfFebruary);

            doNothing().when(emailServiceImpl).sendReminderEmailsToPractices(anyString(), anyString());

            emailServiceImpl.sendReminderEmailsForFebruary(3, false);

            verify(emailServiceImpl, times(1)).sendReminderEmailsToPractices(
                    "ThirdReminder.html",
                    "Third Reminder: Submit Your Future Skills Form"
            );
        }
    }


    @Test
    void testSendReminderEmailsForFebruary_ThirdMonday_FourthReminder_Late() {
        LocalDate thirdMondayOfFebruary = LocalDate.of(2024, Month.FEBRUARY, 19); // Third Monday

        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(thirdMondayOfFebruary);

            doNothing().when(emailServiceImpl).sendReminderEmailsToPractices(anyString(), anyString());

            emailServiceImpl.sendReminderEmailsForFebruary(3, true);

            verify(emailServiceImpl, times(1)).sendReminderEmailsToPractices(
                    "FourthReminder.html",
                    "Fourth Reminder: Submit Your Future Skills Form"
            );
        }
    }

    @Test
    void testSendReminderEmailsForFebruary_FirstMonday_FirstReminder() {
        LocalDate firstMondayOfFebruary = LocalDate.of(2024, Month.FEBRUARY, 5);

        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(firstMondayOfFebruary);

            doNothing().when(emailServiceImpl).sendReminderEmailsToPractices(anyString(), anyString());

            emailServiceImpl.sendReminderEmailsForFebruary(1, false);

            verify(emailServiceImpl, times(1)).sendReminderEmailsToPractices(
                    "FirstReminder.html",
                    "First Reminder: Submit Your Future Skills Form"
            );
        }
    }


}
