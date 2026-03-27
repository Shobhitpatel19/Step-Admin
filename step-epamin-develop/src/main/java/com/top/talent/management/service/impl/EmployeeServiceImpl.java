package com.top.talent.management.service.impl;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.dto.ApiProfileResponse;
import com.top.talent.management.dto.AssessmentDetailsResponse;
import com.top.talent.management.dto.BenchHistoryResponse;
import com.top.talent.management.dto.EmployeeDTO;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.exception.ApiException;
import com.top.talent.management.exception.EmailException;
import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.mapper.ApiMapper;
import com.top.talent.management.mapper.EmployeeMapper;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.service.EmployeeService;
import com.top.talent.management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.top.talent.management.constants.ApiQueryConstants.QUERY_EMAIL;
import static com.top.talent.management.constants.Constants.CLOSED;
import static com.top.talent.management.constants.Constants.DATE_FORMAT;
import static com.top.talent.management.constants.Constants.EPAM_EMAIL_REGEX;
import static com.top.talent.management.constants.Constants.NOT_AVAIALABLE;
import static com.top.talent.management.constants.Constants.UNKNOWN_DATE_FORMAT;
import static com.top.talent.management.constants.Constants.USER_STATUS_ACTIVE;
import static com.top.talent.management.constants.Constants.USER_STATUS_INACTIVE;
import static com.top.talent.management.constants.ErrorMessages.INVALID_EMAIL;
import static com.top.talent.management.constants.ErrorMessages.MISSING_EMAIL;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final UserProfileServiceImpl userProfileService;
    private final UserService userService;
    private final TopTalentEmployeeRepository topTalentEmployeeRepository;
    private final EmployeeMapper employeeMapper;
    private final ApiMapper apiMapper;

    @Override
    public EmployeeDTO getEmployeeProfile(String email) {
        log.info("getting candidateProfile with email: {}", email);

        validateEmail(email);
        ApiProfileResponse.Result result = fetchUserProfile(email);
        UserProfile userProfile = mapUserProfile(result, email);
        EmployeeDTO employeeDTO = createEmployeeDTO(email, result, userProfile);

        fetchAssessmentRecords(userProfile, employeeDTO);
        fetchBenchHistory(userProfile, employeeDTO);
        setDefaultValues(employeeDTO);
        return employeeDTO;
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new EmailException(MISSING_EMAIL);
        }
        email = email.trim();
        Pattern pattern = Pattern.compile(EPAM_EMAIL_REGEX);
        boolean isValidEmail = pattern.matcher(email).matches();
        if (!isValidEmail) {
            log.error("Invalid email format: {}", email);
            throw new EmailException(INVALID_EMAIL);
        }
    }

    private ApiProfileResponse.Result fetchUserProfile(String email) {
        List<ApiProfileResponse.Result> results = userProfileService.fetchUser(QUERY_EMAIL + "'" + email + "'");
        if (results.isEmpty()) {
            log.error(ErrorMessages.NO_RESPONSE_QUERY, QUERY_EMAIL + "'" + email + "'");
            throw new ApiException(ErrorMessages.NO_DATA_FOUND);

        }
        return results.get(0);
    }

    private UserProfile mapUserProfile(ApiProfileResponse.Result result, String email) {
        UserProfile userProfile = apiMapper.mapToUserProfile(result);
        if (userProfile == null) {
            log.warn("User profile not found for email: {}", email);
            throw new ApiException(ErrorMessages.NO_DATA_FOUND);
        }
        return userProfile;
    }

    private EmployeeDTO createEmployeeDTO(String email, ApiProfileResponse.Result result, UserProfile userProfile) {
        UserDTO user = UserDTO.builder().isActive(USER_STATUS_INACTIVE).build();
        try{
            user = userService.getUser(email);
        }
        catch (UserNotFoundException e){
            log.error("User not found for email: {}", email);
        }
        TopTalentEmployee topTalentEmployee = topTalentEmployeeRepository.findByEmail(email).orElse(null);
        EmployeeDTO employeeDTO = employeeMapper.toEmployeeProfileDTO(userProfile, topTalentEmployee, user);
        setActivityStatus(result, employeeDTO);
        setPromotionDate(result, employeeDTO);
        return employeeDTO;
    }

    private void setActivityStatus(ApiProfileResponse.Result result, EmployeeDTO employeeDTO) {
        boolean isActive = result.getEntity() != null && result.getEntity().isActive();
        if (employeeDTO != null) {
            employeeDTO.setIsActive(isActive ? USER_STATUS_ACTIVE : USER_STATUS_INACTIVE);
        }
    }

    private void setPromotionDate(ApiProfileResponse.Result result, EmployeeDTO employeeDTO) {
        employeeDTO.setLastPromotionDate(convertDaysToDate(result.getJobFunctionEffectiveFrom()));
        log.info("Set last promotion date for candidate profile: {}", employeeDTO.getLastPromotionDate());
    }

    private void fetchAssessmentRecords(UserProfile userProfile, EmployeeDTO employeeDTO) {
        log.info("Fetching assessment details for Employment ID: {}", userProfile.getEmploymentId());

        AssessmentDetailsResponse response = fetchAssessmentDetailsResponse(userProfile);

        Optional.ofNullable(response)
                .map(AssessmentDetailsResponse::getResults)
                .filter(results -> !results.isEmpty())
                .flatMap(results -> results.stream()
                        .filter(result -> result.getSessionDateTime() != null && CLOSED.equals(result.getStatus()))
                        .max(Comparator.comparing(result -> Long.parseLong(result.getSessionDateTime()))))
                .ifPresentOrElse(
                        result -> {
                            employeeDTO.setLastAssessmentDate(formatTimestamp(result.getSessionDateTime()));
                            employeeDTO.setLastAssessmentResult(result.getAssessmentResult());
                            log.info("Set last assessment date and result for candidate profile {}",userProfile.getEmail());
                        },
                        () -> {
                            employeeDTO.setLastAssessmentDate(NOT_AVAIALABLE);
                            employeeDTO.setLastAssessmentResult(NOT_AVAIALABLE);
                            log.info("Set last assessment date and result to N/A for candidate profile {}",userProfile.getEmail());
                        }
                );
    }

    private AssessmentDetailsResponse fetchAssessmentDetailsResponse(UserProfile userProfile) {
        try {
            return userProfileService.fetchUserAssessmentRequests(userProfile.getEmploymentId());
        } catch (ApiException e) {
            log.error("Failed to fetch assessment details for Employment ID: {}", userProfile.getEmploymentId(), e);
            throw new ApiException(NOT_AVAIALABLE);
        }
    }
    private void fetchBenchHistory(UserProfile userProfile, EmployeeDTO employeeDTO) {
        log.info("Fetching bench history for Employment ID: {}", userProfile.getEmploymentId());

        BenchHistoryResponse benchHistoryResponse = userProfileService.fetchBenchHistory(userProfile.getEmploymentId());
        String benchStatus = Optional.ofNullable(benchHistoryResponse)
                .map(BenchHistoryResponse::getResults)
                .filter(results -> !results.isEmpty())
                .map(results -> results.get(0))
                .map(BenchHistoryResponse.Result::getEntity)
                .map(BenchHistoryResponse.Result.Entity::getBenchRecords)
                .filter(benchRecords -> !benchRecords.isEmpty())
                .map(benchRecords -> benchRecords.get(benchRecords.size() - 1))
                .map(BenchHistoryResponse.Result.BenchRecord::getStatus)
                .orElse(NOT_AVAIALABLE);

        employeeDTO.setBenchStatus(benchStatus);
        log.info("Set bench status for candidate profile : {},", benchStatus);
    }


    private void setDefaultValues(EmployeeDTO employeeDTO) {
        if (employeeDTO.getTalentProfilePreviousYear() == null) {
            employeeDTO.setTalentProfilePreviousYear(NOT_AVAIALABLE);
            log.info("Set talent profile previous year to N/A for candidate profile {}", employeeDTO.getEmail());
        }
        if (employeeDTO.getTalentProfileCurrentYear() == null) {
            employeeDTO.setTalentProfileCurrentYear(NOT_AVAIALABLE);
            log.info("Set talent profile current year to N/A for candidate profile {}", employeeDTO.getEmail());
        }
    }

    private String formatTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            log.error("Invalid session_date_time: null or empty");
            return NOT_AVAIALABLE;
        }
        try {
            long millis = Long.parseLong(timestamp);
            return new SimpleDateFormat(DATE_FORMAT).format(new Date(millis));
        } catch (NumberFormatException e) {
            log.error("Error parsing session_date_time: {}", timestamp, e);
            return UNKNOWN_DATE_FORMAT;
        }
    }

    private String convertDaysToDate(String jobFunctionEffectiveFrom)  {
        if (jobFunctionEffectiveFrom == null || jobFunctionEffectiveFrom.isEmpty()) {
            log.error("Invalid date: null or empty value received");
            return NOT_AVAIALABLE;
        }
        try {
            long epochDays = Long.parseLong(jobFunctionEffectiveFrom);
            LocalDate date = LocalDate.ofEpochDay(epochDays);
            return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (NumberFormatException e) {
            log.error("Invalid date format: {}", jobFunctionEffectiveFrom, e);
            return UNKNOWN_DATE_FORMAT;
        }

    }

}
