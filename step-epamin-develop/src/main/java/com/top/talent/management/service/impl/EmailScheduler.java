package com.top.talent.management.service.impl;

import com.top.talent.management.entity.EmailSchedule;
import com.top.talent.management.repository.EmailScheduleRepository;
import com.top.talent.management.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static com.top.talent.management.constants.Constants.CRON;

@EnableAsync
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailScheduler {

    private final EmailScheduleRepository emailScheduleRepository;
    private final EmailService emailService;

    @Async
    @Scheduled(cron = CRON)
    public void scheduleFixedRateTaskAsync()  {
        log.info("Email Scheduler invoked - {}" , System.currentTimeMillis() / 1000);
        if(Boolean.TRUE.equals(emailScheduleRepository.existsByDateAndHasMailSent(LocalDate.now(), false)))
        {
            EmailSchedule emailSchedule = emailScheduleRepository.findByDate(LocalDate.now());
            emailService.sendMailToPractices(LocalDate.now(), emailSchedule.getTopTalentExcelVersion());
            log.info("Scheduled emails sent successfully");
        }
    }

    @Scheduled(cron = "0 0 17 ? 2 MON,FRI")
    public void scheduleFebReminders() {
        LocalDate today = LocalDate.now();
        int weekOfMonth = (today.getDayOfMonth() - 1) / 7 + 1;

        if (weekOfMonth <= 3) {
            log.info("Running February reminder scheduler for week: {}, day: {}", weekOfMonth, today.getDayOfWeek());
            DayOfWeek monday = DayOfWeek.MONDAY;
            if (weekOfMonth == 3 && (today.getDayOfWeek() == monday || today.getDayOfWeek() == DayOfWeek.FRIDAY)) {
                boolean isLate = isIsLate(today);
                emailService.sendReminderEmailsForFebruary(3, isLate);
            } else if (today.getDayOfWeek() == monday) {
                emailService.sendReminderEmailsForFebruary(weekOfMonth, false);
            }
        }
    }

    private static boolean isIsLate(LocalDate today) {
        return today.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

}
