package com.top.talent.management.service;

import com.top.talent.management.entity.EmailSchedule;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.repository.EmailScheduleRepository;
import com.top.talent.management.repository.TopTalentExcelVersionRepository;
import com.top.talent.management.service.impl.EmailScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.Month;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class EmailSchedulerTest {

    @Mock
    private EmailScheduleRepository emailScheduleRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private TopTalentExcelVersionRepository topTalentExcelVersionRepository;

    @InjectMocks
    private EmailScheduler emailScheduler;


    @Test
    void testEmailSchedule_whenEmailNotSent_shouldSendEmail() {

        LocalDate currentDate = LocalDate.now();

        TopTalentExcelVersion topTalentExcelVersion = new TopTalentExcelVersion();
        topTalentExcelVersion.setId(1L);

        EmailSchedule emailSchedule = new EmailSchedule();
        emailSchedule.setDate(currentDate);
        emailSchedule.setHasMailSent(false);
        emailSchedule.setTopTalentExcelVersion(topTalentExcelVersion);

        when(emailScheduleRepository.existsByDateAndHasMailSent(currentDate, false)).thenReturn(true);
        when(emailScheduleRepository.findByDate(currentDate)).thenReturn(emailSchedule);

        emailScheduler.scheduleFixedRateTaskAsync();

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<TopTalentExcelVersion> versionCaptor = ArgumentCaptor.forClass(TopTalentExcelVersion.class);

        verify(emailService, times(1)).sendMailToPractices(dateCaptor.capture(), versionCaptor.capture());

    }

    @Test
    void testEmailSchedule_whenNoEmailScheduled_shouldNotSendEmail() {
        LocalDate currentDate = LocalDate.now();

        when(emailScheduleRepository.existsByDateAndHasMailSent(currentDate, false)).thenReturn(false);

        emailScheduler.scheduleFixedRateTaskAsync();

        verify(emailService, never()).sendMailToPractices(any(), any());
    }

    @Test
    void testScheduleFebReminders_ThirdFriday() {
        LocalDate thirdFriday = LocalDate.of(2024, Month.FEBRUARY, 16);
        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(thirdFriday);

            emailScheduler.scheduleFebReminders();

            verify(emailService, times(1)).sendReminderEmailsForFebruary(3, true); // isLate = true for Friday
        }
    }

    @Test
    void testScheduleFebReminders_ThirdMonday() {
        LocalDate thirdMonday = LocalDate.of(2024, Month.FEBRUARY, 19);
        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(thirdMonday);

            emailScheduler.scheduleFebReminders();

            verify(emailService, times(1)).sendReminderEmailsForFebruary(3, false); // isLate = false for Monday
        }
    }

    @Test
    void testScheduleFebReminders_WeekGreaterThanThree() {
        LocalDate fourthMonday = LocalDate.of(2024, Month.FEBRUARY, 26);
        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(fourthMonday);

            emailScheduler.scheduleFebReminders();

            verify(emailService, never()).sendReminderEmailsForFebruary(anyInt(), anyBoolean());
        }
    }
}
