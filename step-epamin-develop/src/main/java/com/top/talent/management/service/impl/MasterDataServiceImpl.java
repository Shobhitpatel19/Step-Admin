package com.top.talent.management.service.impl;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RatingStatus;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.MasterDataResponseDTO;
import com.top.talent.management.dto.TopTalentExcelVersionDTO;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.entity.VersionStatus;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.mapper.ExcelVersionMapper;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.TopTalentExcelVersionRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.repository.VersionStatusRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.IdentificationClosureService;
import com.top.talent.management.service.MailGenerationService;
import com.top.talent.management.service.MasterDataService;
import com.top.talent.management.service.SuperAdminService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.service.UserProfileService;
import com.top.talent.management.service.WeightedScoreCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.top.talent.management.constants.Constants.CULTURAL_SCORE;
import static com.top.talent.management.constants.Constants.HEROES;
import static com.top.talent.management.constants.Constants.STEP;
import static com.top.talent.management.constants.Constants.STEP_2;
import static com.top.talent.management.constants.Constants.UNDERSCORE;
import static com.top.talent.management.constants.Constants.USER_STATUS_ACTIVE;

@Service
@Slf4j
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {


    private final TopTalentExcelVersionService topTalentExcelVersionService;
    private final TopTalentExcelVersionRepository topTalentExcelVersionRepository;
    private final UserRepository userRepository;
    private final ExcelVersionMapper excelVersionMapper;
    private final VersionStatusRepository versionStatusRepository;
    private final WeightedScoreCalculatorService weightedScoreCalculatorService;
    private final TopTalentEmployeeMapper topTalentEmployeeMapper;
    private final TopTalentEmployeeRepository topTalentEmployeeRepository;
    private final SuperAdminService superAdminService;
    private final IdentificationClosureService identificationClosureService;
    private final UserProfileService userProfileService;
    private final MailGenerationService mailGenerationService;


    public MasterDataResponseDTO viewMasterData(String fileName) {
        log.info("Started fetching master data for fileName: {}", fileName);

        String year = String.valueOf(LocalDateTime.now().getYear());
        TopTalentExcelVersion cultureScoreLatestVersion = topTalentExcelVersionService.getExcelVersionForYear(year,STEP+UNDERSCORE+CULTURAL_SCORE+UNDERSCORE+year);
        TopTalentExcelVersion heroesExcelVersion=topTalentExcelVersionService.getExcelVersionForYear(year,STEP+UNDERSCORE+HEROES+UNDERSCORE+year);
        if(cultureScoreLatestVersion!=null && heroesExcelVersion!=null) {

            TopTalentExcelVersion excelVersion = determineExcelVersion(fileName);
            log.info("Determined Excel version: {}", excelVersion);

            List<TopTalentEmployee> topTalentEmployeeDTOS = fetchEmployeeData(excelVersion);
            log.info("Fetched {} employee records for Excel version: {}", topTalentEmployeeDTOS.size(), excelVersion);

            MasterDataResponseDTO responseDTO = buildMasterDataResponse(excelVersion, topTalentEmployeeDTOS);
            log.info("Successfully built master data response for fileName: {}", fileName);

            return responseDTO;
        }else {
            throw new VersionException("Culture, Engx score files are not uploaded yet");

        }



    }

    private TopTalentExcelVersion determineExcelVersion(String fileName) {
        if (fileName != null) {
            return topTalentExcelVersionRepository.findByFileName(fileName)
                    .orElseThrow(() -> {
                        log.error("No Excel version found for fileName: {}", fileName);
                        return new IllegalArgumentException(ErrorMessages.INVALID_FILE_NAME + fileName);
                    });
        }
        return topTalentExcelVersionService.findLatestVersion();
    }

    private List<TopTalentEmployee> fetchEmployeeData(TopTalentExcelVersion excelVersion) {
        log.debug("Fetching employee data for Excel version: {}", excelVersion);
        return topTalentEmployeeRepository.findAllByTopTalentExcelVersion(excelVersion);
    }

    private MasterDataResponseDTO buildMasterDataResponse(TopTalentExcelVersion excelVersion, List<TopTalentEmployee> topTalentEmployees) {
        log.info("Building master data response for Excel version: {}", excelVersion);

        MasterDataResponseDTO responseDTO = new MasterDataResponseDTO();

        List<TopTalentExcelVersionDTO> list = topTalentExcelVersionRepository.findAll()
                .stream()
                .filter(version -> version.getFileName().startsWith(STEP_2))
                .map(excelVersionMapper::convertToDTO)
                .toList();

        responseDTO.setTopTalentExcelVersions(list);

        responseDTO.setNoOfExcelVersion((long) list.size());

        responseDTO.setFetchedExcelVersion(excelVersionMapper.convertToDTO(excelVersion));

        RatingStatus ratingStatus = setRatingStatus(topTalentEmployees);
        responseDTO.setRatingStatus(ratingStatus);

        versionStatusRepository.findByTopTalentExcelVersion(excelVersion)
                .ifPresentOrElse(
                        criteria -> responseDTO.setSubmissionStatus(criteria.getSubmissionStatus()),
                        () -> responseDTO.setSubmissionStatus(SubmissionStatus.NA)
                );

        if (ratingStatus == RatingStatus.COMPLETED) {

            handleCompletedRatingStatus(responseDTO, excelVersion, topTalentEmployees);
        } else {
            responseDTO.setPracticeHeadListDetailed(getPracticeHeadsDetailed(topTalentEmployees));
            responseDTO.setTopTalentEmployeeDTOList(topTalentEmployees.stream().map(topTalentEmployeeMapper::employeeDataToEmployeeDTO).toList());
            responseDTO.setFilteredTopTalentEmployees(Constants.EMPTY_TOP_TALENT_LIST);
        }

        log.info("Master data response built successfully for Excel version: {}", excelVersion);
        return responseDTO;
    }

    private Map<Long, UserProfile> getUserProfileMap(List<TopTalentEmployee> topTalentEmployees) {
        Set<Long> uidSet = topTalentEmployees.stream()
                .map(TopTalentEmployee::getUid)
                .collect(Collectors.toSet());
        Set<UserProfile> userProfiles = userProfileService.fetchUserDetails(uidSet);
        Map<Long, UserProfile> userProfileMap =userProfiles.stream().collect(Collectors.toMap(UserProfile::getUid, userProfile -> userProfile));
        log.debug("User profile map fetched successfully. Map size: {}", userProfileMap.size());
        return userProfileMap;
    }

    private void handleCompletedRatingStatus(MasterDataResponseDTO responseDTO, TopTalentExcelVersion excelVersion, List<TopTalentEmployee> topTalentEmployees) {

        if (checkIfWeightedMeanScore(topTalentEmployees)) {
            log.debug("Calculating and assigning weighted scores.");

            weightedScoreCalculatorService.calculateAndAssignWeightedScores();
        }

        responseDTO.setTopTalentEmployeeDTOList(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(excelVersion)
                .stream()
                .map(topTalentEmployeeMapper::employeeDataToEmployeeDTO)
                .toList());

        responseDTO.setFilteredTopTalentEmployees(topTalentEmployeeRepository
                .findAllByTopTalentExcelVersionAndIsStepUser(excelVersion, true)
                .stream()
                .map(topTalentEmployeeMapper::employeeDataToEmployeeDTO)
                .toList());

        log.info("Completed rating status handled successfully for Excel version: {}", excelVersion);
    }

    private boolean checkIfWeightedMeanScore(List<TopTalentEmployee> topTalentEmployees) {
        return topTalentEmployees.stream().anyMatch(emp -> emp.getOverallWeightedScoreForMerit() == null);
    }

    private static RatingStatus setRatingStatus(List<TopTalentEmployee> topTalentEmployees) {
        log.debug("Setting rating status for employees.");
        RatingStatus status = topTalentEmployees.stream().anyMatch(employee -> employee.getPracticeRating() == null)
                ? RatingStatus.PARTIALLY_COMPLETED : RatingStatus.COMPLETED;
        log.debug("Rating status set: {}", status);
        return status;
    }

    private Map<String, Map<String, List<String>>> getPracticeHeadsDetailed(List<TopTalentEmployee> topTalentEmployees) {
        Map<String, Map<String, List<String>>> practiceHeadsDetailed = topTalentEmployees.stream()
                .filter(topTalentEmployee -> topTalentEmployee.getPracticeRating() == null)
                .collect(Collectors.groupingBy(
                        TopTalentEmployee::getCompetencyPractice,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                employees -> {
                                    String practiceHeadName = getPracticeHeadByPractice(employees.get(0).getCompetencyPractice());
                                    return Map.of(practiceHeadName, employees.stream()
                                            .map(TopTalentEmployee::getName)
                                            .toList());
                                })
                ));
        log.debug("Detailed practice heads fetched successfully. Size: {}", practiceHeadsDetailed.size());
        return practiceHeadsDetailed;
    }


    private String getPracticeHeadByPractice(String practice) {
        List<User> practiceHead = userRepository.findUserByPracticeAndRole(practice, new Role(4L, "P"));

        Optional<User> practiceH = practiceHead.stream().filter(user -> !user.isDelegate() && user.getStatus().equals(USER_STATUS_ACTIVE)).findFirst();

        return practiceH.map(user -> user.getFirstName() + " " + user.getLastName()).orElse("NA");
    }


    @Override
    public MasterDataResponseDTO saveEmployeesOfExcelVersion(SubmissionStatus submissionStatus, List<Long> uidS, CustomUserPrincipal customUserPrincipal) {
        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();

        List<TopTalentEmployee> topTalentEmployees = topTalentEmployeeRepository.findAllByTopTalentExcelVersion(latestVersion);
        topTalentEmployees.forEach(emp -> emp.setIsStepUser(false));
        topTalentEmployeeRepository.saveAll(topTalentEmployees);
        log.info("Set 'isStepUser' to false for all existing employees");

        List<TopTalentEmployee> topTalentEmployeeList = topTalentEmployeeRepository.findAllByUidInAndTopTalentExcelVersion(uidS, latestVersion);

        Optional<VersionStatus> optionalFilterCriteria = versionStatusRepository.findByTopTalentExcelVersion(latestVersion);
        if (optionalFilterCriteria.isPresent()) {
            optionalFilterCriteria.get().setSubmissionStatus(submissionStatus);
            versionStatusRepository.save(optionalFilterCriteria.get());

            log.info("Updated the submission status in FilterCriteria for version: {}", latestVersion.getVersionName());
        } else {
            log.info("No FilterCriteria found for version: {}", latestVersion.getVersionName());
        }

        topTalentEmployeeList.forEach(emp -> emp.setIsStepUser(true));
        topTalentEmployeeRepository.saveAll(topTalentEmployeeList);
        log.info("Set 'isStepUser' to true for employees with specific UIDs");

        if (submissionStatus == SubmissionStatus.S) {
            log.info("SubmissionStatus is 'S'. Granting additional admin access and closing identification phase.");
            List<UserDTO> savedUsers = superAdminService.grantAccessToUserRole();
            savedUsers.forEach(user ->
                    mailGenerationService.
                            generatePracticeRemainderMailAndSend(user.getEmail(),
                                    Constants.USER_WELCOME_MAIL_SUBJECT,
                                    Constants.USER_WELCOME_MAIL_TEMPLATE,
                                    user.getFirstName()+" "+user.getLastName()));
            identificationClosureService.endPhase(customUserPrincipal);
        } else {
            log.info("No additional admin tasks are necessary for SubmissionStatus: {}", submissionStatus);
        }

        return buildMasterDataResponse(latestVersion, topTalentEmployeeList);
    }

    @Override
    public Map<Long, UserProfile> getUserProfile() {
        List<TopTalentEmployee> topTalentEmployees=topTalentEmployeeRepository.findAllByTopTalentExcelVersion(topTalentExcelVersionService.findLatestVersion());
        return getUserProfileMap(topTalentEmployees);
    }
}
