package com.top.talent.management.service.impl;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.EmailSchedule;
import com.top.talent.management.entity.NotificationManagement;
import com.top.talent.management.entity.PracticeEmailScheduleLog;
import com.top.talent.management.entity.Role;
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
import com.top.talent.management.service.EmailService;
import com.top.talent.management.service.MailGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.IntStream;

import static com.top.talent.management.constants.Constants.NO_OF_PLUS_DAYS_FOR_MONDAY;
import static com.top.talent.management.constants.Constants.NO_OF_PLUS_DAYS_FOR_NOT_MONDAY;
import static com.top.talent.management.constants.Constants.SUBJECTS;
import static com.top.talent.management.constants.Constants.SYSTEM;
import static com.top.talent.management.constants.Constants.TEMPLATES;
import static com.top.talent.management.constants.Constants.SUBJECT_OF_FUTURE_SKILLS;
import static com.top.talent.management.constants.Constants.TEMPLATE_OF_FUTURE_SKILLS;
import static com.top.talent.management.constants.ErrorMessages.ERROR_SAVING_FILE;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final UserRepository userRepository;
    private final TopTalentEmployeeRepository topTalentEmployeeRepository;
    private final EmailScheduleRepository emailScheduleRepository;
    private final PracticeEmailScheduleLogRepository practiceEmailScheduleLogRepository;
    private final MailGenerationService mailGenerationService;
    private final NotificationManagementRepository notificationManagementRepository;

    private final RoleRepository roleRepository;


    public void generateDatesAndTemplates(LocalDate startDate, TopTalentExcelVersion topTalentExcelVersion)
    {
        Queue<String> emailSubjects = new ArrayDeque<>();
        Queue<String> templateList = new ArrayDeque<>();

        IntStream.rangeClosed(0, 4).forEach(i -> {
            emailSubjects.add(SUBJECTS.get(i));
            templateList.add(TEMPLATES.get(i));
        });

        if(startDate.getDayOfWeek() == DayOfWeek.SATURDAY)
        {
            startDate = startDate.plusDays(2);
        } else if (startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            startDate = startDate.plusDays(1);
        }

        Queue<LocalDate> queueOfDates = new ArrayDeque<>();
        queueOfDates.add(startDate);

        List<Integer> plusDays = new ArrayList<>();
        if(startDate.getDayOfWeek() == DayOfWeek.MONDAY)
        {
            plusDays.addAll(NO_OF_PLUS_DAYS_FOR_MONDAY);
        } else
        {
            plusDays.addAll(NO_OF_PLUS_DAYS_FOR_NOT_MONDAY);
        }
        queueOfDates.addAll(
                plusDays.stream()
                        .map(startDate::plusDays)
                        .toList()
        );

        try
        {
            IntStream.range(0, Math.min(queueOfDates.size(), Math.min(templateList.size(), emailSubjects.size())))
                    .mapToObj(i -> EmailSchedule.builder()
                            .isFirstDate(i == 0)
                            .date(queueOfDates.poll())
                            .emailTemplate(templateList.poll())
                            .emailSubject(emailSubjects.poll())
                            .hasMailSent(false)
                            .topTalentExcelVersion(topTalentExcelVersion)
                            .created(LocalDateTime.now())
                            .createdBy(SYSTEM)
                            .lastUpdatedBy(SYSTEM)
                            .lastUpdated(LocalDateTime.now())
                            .build()
                    )
                    .forEach(emailScheduleRepository::save);

        }
        catch (Exception e)
        {
            log.error(ErrorMessages.EMAIL_SCHEDULE_NOT_SAVED,e);
            throw new EmailException(ERROR_SAVING_FILE);
        }
    }

    public void sendMailToPractices(LocalDate date, TopTalentExcelVersion topTalentExcelVersion) {

        if(Boolean.TRUE.equals(emailScheduleRepository.existsByDate(date))){
            Role practiceRole = roleRepository.findByName(RoleConstants.PRACTICE);

            List<User> practiceHeads =  userRepository.findAllByRole(practiceRole);
            if(practiceHeads.isEmpty())
            {
                log.info("no practice head(s) in users table");
                return;
            }
            EmailSchedule emailSchedule = emailScheduleRepository.findByDate(date);

            practiceHeads.stream()
                    .map(practiceHead -> {
                        List<TopTalentEmployee> employeesList = topTalentEmployeeRepository.findAllByCompetencyPracticeAndPracticeRatingIsNullAndTopTalentExcelVersion(
                                practiceHead.getPractice(), topTalentExcelVersion);
                        return Map.entry(practiceHead, employeesList);
                    })
                    .forEach(entry -> {
                        User practiceHead = entry.getKey();
                        List<TopTalentEmployee> employeesList = entry.getValue();
                        boolean isPracticeRatingNotificationEnabled = notificationManagementRepository.findByUserUuidAndCategoryCategoryId(practiceHead.getUuid(), 2L)
                                .map(NotificationManagement::isNotificationsEnabled)
                                .orElse(false);


                        if(!Boolean.TRUE.equals(emailScheduleRepository.findByDate(LocalDate.now()).getIsFirstDate()))
                        {
                            if(isPracticeRatingNotificationEnabled){
                                mailGenerationService.generatePracticeRemainderMailAndSend(
                                        practiceHead.getEmail(),
                                        emailSchedule.getEmailSubject(),
                                        emailSchedule.getEmailTemplate(),
                                        (practiceHead.getFirstName() + " " + practiceHead.getLastName()),
                                        employeesList,
                                        topTalentExcelVersion
                                ,practiceHead.getPractice());
                            }
                            else {
                                log.info("Practice Rating notifications are disabled for Practice Head: {}. Skipping reminder emails.", practiceHead.getEmail());
                            }
                        }
                        else {
                            mailGenerationService.generatePracticeRemainderMailAndSend(practiceHead.getEmail(), emailSchedule.getEmailSubject(), emailSchedule.getEmailTemplate(), (practiceHead.getFirstName() + " " + practiceHead.getLastName()));
                        }

                        if (Boolean.FALSE.equals(practiceEmailScheduleLogRepository.existsByPracticeEmailIdAndTopTalentExcelVersion(practiceHead.getEmail(), topTalentExcelVersion))) {
                            PracticeEmailScheduleLog log = PracticeEmailScheduleLog.builder()
                                    .practiceEmailId(practiceHead.getEmail())
                                    .lastEmailSentDate(LocalDate.now())
                                    .noOfEmailsSent(1L)
                                    .topTalentExcelVersion(topTalentExcelVersion)
                                    .created(LocalDateTime.now())
                                    .createdBy(SYSTEM)
                                    .lastUpdatedBy(SYSTEM)
                                    .lastUpdated(LocalDateTime.now())
                                    .build();
                            practiceEmailScheduleLogRepository.save(log);

                        } else {
                            PracticeEmailScheduleLog log = practiceEmailScheduleLogRepository.findByPracticeEmailId(practiceHead.getEmail());
                            log.setLastEmailSentDate(LocalDate.now());
                            log.setNoOfEmailsSent(log.getNoOfEmailsSent() + 1);
                            practiceEmailScheduleLogRepository.save(log);
                        }
                    });

            EmailSchedule schedule = emailScheduleRepository.findByDate(date);
            schedule.setHasMailSent(true);
            emailScheduleRepository.save(schedule);
        }
    }

    public void sendMailToAdmin(CustomUserPrincipal userPrincipal, String practiceName) {
        log.info("Triggering email notification to Admins for Future Skills submission by Practice Head: {}", userPrincipal.getFullName());

        Role adminRole = roleRepository.findByName(RoleConstants.SUPER_ADMIN);
        List<User> admins = userRepository.findAllByRole(adminRole);
        if (admins.isEmpty()) {
            log.warn("No Admin users found. Skipping email notification.");
            return;
        }

        String practiceHeadName = userPrincipal.getFullName();
        admins.forEach(admin -> {
            try {
                log.info("Preparing email for Admin: {} regarding Practice Head: {}", admin.getEmail(), practiceHeadName);

                mailGenerationService.generateAdminMailAndSend(
                        admin.getEmail(),
                        SUBJECT_OF_FUTURE_SKILLS,
                        TEMPLATE_OF_FUTURE_SKILLS,
                        admin.getFirstName(),
                        practiceHeadName,
                        practiceName
                );
                log.info("Email notification sent to Admin: {}, Practice Head: {}", admin.getEmail(), practiceHeadName);
            } catch (Exception e) {
                log.error("Failed to send email notification to Admin: {}, Error: {}", admin.getEmail(), e.getMessage());
            }
        });
    }

    public void sendReminderEmailsToPractices(String templateFilename, String subject) {
        Role practiceRole = roleRepository.findByName(RoleConstants.PRACTICE);
        List<User> practiceHeads = userRepository.findAllByRole(practiceRole);

        if (practiceHeads.isEmpty()) {
            log.info("No practice head(s) found in the users table.");
            return;
        }

        practiceHeads.forEach(practiceHead -> {
            String recipientName = practiceHead.getFirstName() + " " + practiceHead.getLastName();
            log.info("Sending email to Practice Head: {}, Email: {}", recipientName, practiceHead.getEmail());

            try {
                mailGenerationService.generateReminderMailAndSend(
                        practiceHead.getEmail(),
                        subject,
                        templateFilename,
                        recipientName
                );
                log.info("Email sent successfully to: {}", practiceHead.getEmail());
            } catch (Exception e) {
                log.error("Failed to send email to: {}, Error: {}", practiceHead.getEmail(), e.getMessage());
            }
        });
    }

    @Override
    public void sendReminderEmailsForFebruary(int weekOfMonth, boolean isLateReminder) {
        LocalDate today = LocalDate.now();
        LocalDate firstMondayOfFebruary = LocalDate.of(today.getYear(), Month.FEBRUARY, 1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        LocalDate firstMondayOfMonth = today.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        LocalDate firstFridayOfMonth = today.with(TemporalAdjusters.firstInMonth(DayOfWeek.FRIDAY));

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(firstMondayOfMonth, today);
        int mondayOccurrence = (int)(daysBetween / 7) + 1;
        long daysBetweenFriday = java.time.temporal.ChronoUnit.DAYS.between(firstFridayOfMonth, today);
        int fridayOccurrence = (int)(daysBetweenFriday / 7) + 1;
        if(today.getDayOfWeek().equals(DayOfWeek.FRIDAY))
        {
            if(fridayOccurrence == 3)
            {
                sendReminderEmailsToPractices("FourthReminder.html" , "Fourth Reminder: Submit Your Future Skills Form");
            }
            else{
                log.info("Not Third Friday");
                return;
            }
        }
        LocalDate targetDate;
        String templateFilename;
        String subject;
        switch (mondayOccurrence) {
            case 1:
                targetDate = firstMondayOfFebruary;
                templateFilename = "FirstReminder.html";
                subject = "First Reminder: Submit Your Future Skills Form";
                break;
            case 2:
                targetDate = firstMondayOfFebruary.plusWeeks(1);
                templateFilename = "SecondReminder.html";
                subject = "Second Reminder: Submit Your Future Skills Form";
                break;
            case 3:
                if (isLateReminder) {
                    targetDate = firstMondayOfFebruary.plusWeeks(2).with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
                    templateFilename = "FourthReminder.html";
                    subject = "Fourth Reminder: Submit Your Future Skills Form";
                } else {
                    targetDate = firstMondayOfFebruary.plusWeeks(2);
                    templateFilename = "ThirdReminder.html";
                    subject = "Third Reminder: Submit Your Future Skills Form";
                }
                break;
            default:
                log.warn("Invalid week of month: {}", weekOfMonth);
                return;
        }

        log.info("Calculated targetDate for week {}: {}" , weekOfMonth, targetDate);
        sendReminderEmailsToPractices(templateFilename , subject);
        log.info("Mail has been sent");
    }
}