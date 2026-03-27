package com.top.talent.management.service.impl;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.NumericConstants;
import com.top.talent.management.constants.PracticeDelegationFeaturesConstants;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.FutureSkillCategoryResponseDTO;
import com.top.talent.management.dto.FutureSkillPracticeDTO;
import com.top.talent.management.dto.FutureSkillRequestListDTO;
import com.top.talent.management.dto.FutureSkillResponseDTO;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserResponseDTO;
import com.top.talent.management.entity.FutureSkill;
import com.top.talent.management.entity.FutureSkillCategory;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.FutureSkillException;
import com.top.talent.management.exception.ResourceNotFoundException;
import com.top.talent.management.repository.FutureSkillCategoryRepository;
import com.top.talent.management.repository.FutureSkillRepository;
import com.top.talent.management.repository.NotificationManagementRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.FutureSkillService;
import com.top.talent.management.service.IdentificationClosureService;
import com.top.talent.management.service.MailGenerationService;
import com.top.talent.management.service.PracticeDelegateUserService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.service.UserService;
import com.top.talent.management.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class FutureSkillServiceImpl implements FutureSkillService {

    private final FutureSkillRepository futureSkillRepository;
    private final FutureSkillCategoryRepository futureSkillCategoryRepository;
    private final TopTalentExcelVersionService topTalentExcelVersionService;
    private final UserService userService;
    private final MailGenerationService mailGenerationService;
    private final IdentificationClosureService identificationClosureService;
    private final PracticeDelegateUserService practiceDelegateUserService;
    private final NotificationManagementRepository notificationManagementRepository;
    private final EmailService emailService;

    @Override
    public FutureSkillResponseDTO getFutureSkill(CustomUserPrincipal userPrincipal) {
        log.info("Fetching future skills for Practice Head: {}", userPrincipal.getFullName());

        String practice = userService.getUser(userPrincipal.getEmail()).getPractice();
        String practiceHeadName;
        if(RoleConstants.ROLE_USER.equals(userPrincipal.getRole()) ||
                (RoleConstants.ROLE_PRACTICE.equals(userPrincipal.getRole())) &&
                        Boolean.TRUE.equals(userPrincipal.isDelegate())){
            UserDTO practiceHead = userService.getPracticeHeadByCompetency(practice);
            practiceHeadName = practiceHead.getFirstName()+" "+practiceHead.getLastName();
        }
        else{
           practiceHeadName = userPrincipal.getFullName();
        }
        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();

        List<FutureSkillCategoryResponseDTO> categories;
        SubmissionStatus submissionStatus = SubmissionStatus.NA;
        LocalDateTime lastUpdated = null;

        if (latestVersion != null && futureSkillRepository.existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                 practice, latestVersion, false)) {
            log.info("Skills found for the latest version: {}", latestVersion.getVersionName());
            List<FutureSkill> skillsForLatestVersion = futureSkillRepository
                    .findByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                    practice, latestVersion, false);

            skillsForLatestVersion.sort(Comparator.comparing(skill -> skill.getFutureSkillCategory().getId()));
            categories = mapSkillsToCategories(skillsForLatestVersion);
            lastUpdated = skillsForLatestVersion.stream()
                    .map(FutureSkill::getLastUpdated)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            submissionStatus = skillsForLatestVersion.get(0).getSubmissionStatus();
        } else {
            log.info("No skills found for the latest version. Checking previous version...");
            TopTalentExcelVersion previousVersion = topTalentExcelVersionService.getPreviousYearVersion();

            if (!Constants.NO_VERSION_NAME_NA.equals(previousVersion.getVersionName()) &&
                    futureSkillRepository.existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                    practice, previousVersion, false)) {
                log.info("Skills found for the previous version: {}", previousVersion.getVersionName());
                List<FutureSkill> skillsForPreviousVersion =
                        futureSkillRepository.findByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                       practice, previousVersion, false);

                skillsForPreviousVersion.sort(Comparator.comparing(skill -> skill.getFutureSkillCategory().getId()));
                categories = mapSkillsToCategories(skillsForPreviousVersion);
                lastUpdated = skillsForPreviousVersion.stream()
                        .map(FutureSkill::getLastUpdated)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);
                submissionStatus = skillsForPreviousVersion.get(0).getSubmissionStatus();
            } else {
                log.info("No skills found for the previous version. Using default categories.");
                categories = fetchDefaultCategories();
            }
        }

        log.info("Future skills fetched successfully for Practice Head: {}", userPrincipal.getFullName());
        return FutureSkillResponseDTO.builder()
                .practiceName(practice)
                .practiceHeadName(practiceHeadName)
                .submissionStatus(submissionStatus)
                .categories(categories)
                .lastUpdated(lastUpdated)
                .build();
    }

    @Override
    @Transactional
    public String saveFutureSkill(CustomUserPrincipal userPrincipal, FutureSkillRequestListDTO futureSkillRequestListDTO) {
        log.info("Saving future skills for Practice Head: {}", userPrincipal.getFullName());

        if (Boolean.TRUE.equals(userPrincipal.isDelegate()) &&
                !Boolean.TRUE.equals(practiceDelegateUserService.hasAccessToFeature(PracticeDelegationFeaturesConstants.FUTURE_SKILL_FEATURE))) {
            throw new FutureSkillException(ErrorMessages.PRACTICE_FORM_NO_APPROVAL_PERMISSION);
        }

        if (userPrincipal.isDelegate() && futureSkillRequestListDTO.getSubmissionStatus().equals(SubmissionStatus.A)
                && Boolean.TRUE.equals(practiceDelegateUserService.isApprovalRequired())) {
            throw new FutureSkillException(ErrorMessages.PRACTICE_FORM_NO_APPROVAL_PERMISSION);
        }
        String loggedInUserName = userPrincipal.getFullName();
        LocalDateTime now = LocalDateTime.now();
        UserDTO user = userService.getUser(userPrincipal.getEmail());
        String practiceName = user.getPractice();

        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();

        boolean isConsiderForAspirationRating = calculateAspirationRatingEligibility(now);

        futureSkillRequestListDTO.getFutureSkills().forEach(request -> {
            FutureSkillCategory category = futureSkillCategoryRepository.findByCategoryName(request.getCategoryName())
                    .orElseThrow(() -> {
                        log.error("Future Skill Category not found: {}", request.getCategoryName());
                        return new FutureSkillException(ErrorMessages.FUTURE_SKILLS_CATEGORY_NOT_FOUND + request.getCategoryName());
                    });

            if (isConsiderForAspirationRating) {
                Optional<FutureSkill> existingAspirationSkill =
                        futureSkillRepository.findByPracticeNameAndFutureSkillCategoryAndTopTalentExcelVersionAndIsForAspirationRating(
                        practiceName, category, latestVersion, true);

                FutureSkill aspirationSkill = existingAspirationSkill.orElse(FutureSkill.builder()
                        .practiceName(practiceName)
                        .topTalentExcelVersion(latestVersion)
                        .futureSkillCategory(category)
                        .answer(request.getAnswer())
                        .isForAspirationRating(true)
                        .build());

                aspirationSkill.setSubmissionStatus(futureSkillRequestListDTO.getSubmissionStatus());
                aspirationSkill.setAnswer(request.getAnswer());
                aspirationSkill.setLastUpdated(now);
                aspirationSkill.setLastUpdatedBy(loggedInUserName);

                if (existingAspirationSkill.isEmpty()){
                    aspirationSkill.setCreated(now);
                    aspirationSkill.setCreatedBy(loggedInUserName);
                }

                futureSkillRepository.save(aspirationSkill);
            }

            Optional<FutureSkill> existingRegularSkill =
                    futureSkillRepository.findByPracticeNameAndFutureSkillCategoryAndTopTalentExcelVersionAndIsForAspirationRating(
                    practiceName, category, latestVersion, false);

            FutureSkill regularSkill = existingRegularSkill.orElse(FutureSkill.builder()
                    .practiceName(practiceName)
                    .topTalentExcelVersion(latestVersion)
                    .futureSkillCategory(category)
                    .answer(request.getAnswer())
                    .isForAspirationRating(false)
                    .build());

            regularSkill.setSubmissionStatus(futureSkillRequestListDTO.getSubmissionStatus());
            regularSkill.setAnswer(request.getAnswer());
            regularSkill.setLastUpdated(now);
            regularSkill.setLastUpdatedBy(loggedInUserName);
            if(existingRegularSkill.isEmpty()){
                regularSkill.setCreatedBy(loggedInUserName);
                regularSkill.setCreated(now);
            }

            futureSkillRepository.save(regularSkill);
        });

        log.info("Future skills saved successfully for Practice Head: {}", userPrincipal.getFullName());
        if ( Boolean.TRUE.equals(userPrincipal.isDelegate()) && futureSkillRequestListDTO.getSubmissionStatus() == SubmissionStatus.A) {
            emailService.sendMailToAdmin(userPrincipal, practiceName);
        } else if (!Boolean.TRUE.equals(userPrincipal.isDelegate()) && (futureSkillRequestListDTO.getSubmissionStatus() == SubmissionStatus.S
                || futureSkillRequestListDTO.getSubmissionStatus() == SubmissionStatus.A)){
            emailService.sendMailToAdmin(userPrincipal, practiceName);
        }
        return getMessage(futureSkillRequestListDTO.getSubmissionStatus());
    }

    @Override
    public List<FutureSkillPracticeDTO> getPracticeDetailsAndSubmissionStatus() {
        log.info("Fetching practice details and submission status for all Practice Heads.");

        List<UserResponseDTO> practiceHeadList = userService.getUsersByRole(RoleConstants.PRACTICE,
                Optional.of(Constants.USER_STATUS_ACTIVE));
        if (practiceHeadList.isEmpty()) {
            throw new ResourceNotFoundException("Practice Head not found for this Role.");
        }
        List<FutureSkillPracticeDTO> futureSkillPracticeDTOS = new ArrayList<>();

        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();
        log.info("Latest version for future skills: {}", latestVersion.getVersionName());

        for (UserResponseDTO practiceHead : practiceHeadList) {
            log.info("Processing Practice Head: {}", practiceHead.getFirstName() + " " + practiceHead.getLastName());

            SubmissionStatus submissionStatus = SubmissionStatus.NA;
            String date = "NA";
            String futureSkill ="NA";
            String submittedBy = "NA";
            boolean submissionExists =
                    futureSkillRepository.existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                    practiceHead.getPractice(),
                    latestVersion,
                    false
            );
            if (submissionExists) {
                log.info("Submission found for Practice Head Role.");
                List<FutureSkill> futureSkills =
                        futureSkillRepository.findByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(
                        practiceHead.getPractice(),
                        latestVersion,
                        false
                );
                submissionStatus = futureSkills.get(0).getSubmissionStatus();
                date = futureSkills.get(0).getLastUpdated().toLocalDate().toString();
                Optional<FutureSkillCategory> category = futureSkillCategoryRepository.findByCategoryName("Future Skills (3–5 years)");
                if(category.isPresent()) {
                    futureSkill = futureSkills.stream()
                            .filter(f -> category.get().equals(f.getFutureSkillCategory())).findFirst().map(FutureSkill::getAnswer).orElse("NA");

                }
                submittedBy = futureSkills.get(0).getLastUpdatedBy();

            }
            futureSkillPracticeDTOS.add(
                    FutureSkillPracticeDTO.builder()
                            .practiceName(practiceHead.getPractice())
                            .practiceHeadName(practiceHead.getFirstName() + " " + practiceHead.getLastName())
                            .submissionStatus(submissionStatus.name())
                            .skills(futureSkill)
                            .date(date)
                            .submittedBy(submittedBy)
                            .build()
            );
        }
        return futureSkillPracticeDTOS;
    }

    @Override
    public void notifyIfIdentificationPhaseEnded() {
        List<UserDTO> userDTOList = userService.getUsersWithPracticeHeadRole();
        String subject = Constants.FUTURE_SKILL_EMAIL_SUBJECT;
        String fileName = Constants.FUTURE_SKILL_EMAIL_FILENAME;

        userDTOList.stream()
                .filter(userDTO -> Constants.USER_STATUS_ACTIVE.equals(userDTO.getIsActive()))
                .forEach(userDTO -> {
                    String toEmail = userDTO.getEmail();
                    String practiceHeadName = userDTO.getFirstName() + " " + userDTO.getLastName();
                    log.info("Sending email to practice head: {}", toEmail);
                    mailGenerationService.generatePracticeRemainderMailAndSend(toEmail, subject, fileName, practiceHeadName);
                });
        log.info("Emails have been sent to all practice heads.");
    }

    private List<FutureSkillCategoryResponseDTO> mapSkillsToCategories(List<FutureSkill> skills) {
        return skills.stream()
                .sorted(Comparator.comparing(skill -> skill.getFutureSkillCategory().getId()))
                .map(skill -> FutureSkillCategoryResponseDTO.builder()
                        .categoryName(skill.getFutureSkillCategory().getCategoryName())
                        .questions(skill.getFutureSkillCategory().getQuestions())
                        .answer(skill.getAnswer())
                        .build())
                .toList();
    }

    private List<FutureSkillCategoryResponseDTO> fetchDefaultCategories() {
        return futureSkillCategoryRepository.findAll(Sort.by("id"))
                .stream()
                .map(category -> FutureSkillCategoryResponseDTO.builder()
                        .categoryName(category.getCategoryName())
                        .questions(category.getQuestions())
                        .answer(null)
                        .build())
                .toList();
    }

    private boolean calculateAspirationRatingEligibility(LocalDateTime now) {
        if (!identificationClosureService.isIdentificationClosureDataPresent() || !identificationClosureService.isPhaseClosed()) return true;
        return now.isBefore(identificationClosureService.latestPhaseEndDate().plusWeeks(NumericConstants.FUTURE_SKILLS_NUMBER_OF_WEEKS));
    }

    private String getMessage(SubmissionStatus status) {
        return switch (status) {
            case NA -> Constants.FUTURE_SKILL_STATUS_NA;
            case D -> Constants.FUTURE_SKILL_STATUS_D;
            case S -> Constants.FUTURE_SKILL_STATUS_S;
            case A -> Constants.FUTURE_SKILL_STATUS_A;
        };
    }
}