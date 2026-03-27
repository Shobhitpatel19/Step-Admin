package com.top.talent.management.service;


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
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.service.impl.EmployeeServiceImpl;
import com.top.talent.management.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.top.talent.management.constants.Constants.NOT_AVAIALABLE;
import static com.top.talent.management.constants.Constants.UNKNOWN_DATE_FORMAT;
import static com.top.talent.management.constants.Constants.USER_STATUS_ACTIVE;
import static com.top.talent.management.constants.Constants.USER_STATUS_INACTIVE;
import static com.top.talent.management.constants.ErrorMessages.INVALID_EMAIL;
import static com.top.talent.management.constants.ErrorMessages.MISSING_EMAIL;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private UserProfileServiceImpl userProfileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopTalentEmployeeRepository topTalentEmployeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private ApiMapper apiMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private ApiProfileResponse.Result result;

    @Mock
    private UserProfile userProfile;

    @Mock
    private UserService userService;

    @Mock
    private TopTalentEmployee topTalentEmployee;

    @Mock
    private EmployeeDTO employeeDTO;

    private String validEmail;
    private String invalidEmail;
    private String emptyEmail;

    @BeforeEach
    void setUp() {
        validEmail = "amit_mani@epam.com";
        invalidEmail = "amit_mani@epam.co";
        emptyEmail = "";
        result = new ApiProfileResponse.Result();
        userProfile = UserProfile.builder().build();
        topTalentEmployee = new TopTalentEmployee();
        employeeDTO = EmployeeDTO.builder().build();
    }



    @Test
    void testGetEmployeeProfile_InvalidEmail() {
        EmailException exception = assertThrows(EmailException.class, () -> employeeService.getEmployeeProfile(invalidEmail));
        assertEquals(INVALID_EMAIL, exception.getMessage());
    }

    @Test
    void testGetEmployeeProfile_EmptyEmail() {
        EmailException exception = assertThrows(EmailException.class, () -> employeeService.getEmployeeProfile(emptyEmail));
        assertEquals(MISSING_EMAIL, exception.getMessage());
    }

    @Test
    void testGetEmployeeProfile_UserNotFound() {
        when(userProfileService.fetchUser(anyString())).thenReturn(Collections.emptyList());

     ApiException exception = assertThrows(ApiException.class, () -> employeeService.getEmployeeProfile(validEmail));
        assertEquals(ErrorMessages.NO_DATA_FOUND, exception.getMessage());
    }

    @Test
    void testValidateEmail_ValidEmail() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("validateEmail", String.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(employeeService, validEmail));
    }

    @Test
    void testFetchUserProfile_Success() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("fetchUserProfile", String.class);
        method.setAccessible(true);
        when(userProfileService.fetchUser(anyString())).thenReturn(List.of(result));

        ApiProfileResponse.Result fetchedResult = (ApiProfileResponse.Result) method.invoke(employeeService, validEmail);

        assertNotNull(fetchedResult);
        verify(userProfileService, times(1)).fetchUser(anyString());
    }

    @Test
    void testMapUserProfile_Success() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("mapUserProfile", ApiProfileResponse.Result.class, String.class);
        method.setAccessible(true);
        when(apiMapper.mapToUserProfile(result)).thenReturn(userProfile);

        UserProfile mappedUserProfile = (UserProfile) method.invoke(employeeService, result, validEmail);

        assertNotNull(mappedUserProfile);
        verify(apiMapper, times(1)).mapToUserProfile(result);
    }


    @Test
    void testSetDefaultValues() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("setDefaultValues", EmployeeDTO.class);
        method.setAccessible(true);
        employeeDTO.setTalentProfilePreviousYear(null);
        employeeDTO.setTalentProfileCurrentYear(null);

        method.invoke(employeeService, employeeDTO);

        assertEquals(NOT_AVAIALABLE, employeeDTO.getTalentProfilePreviousYear());
        assertEquals(NOT_AVAIALABLE, employeeDTO.getTalentProfileCurrentYear());
    }

    @Test
    void testFormatTimestamp_ValidTimestamp() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("formatTimestamp", String.class);
        method.setAccessible(true);
        String timestamp = "1633046400000"; // Example timestamp
        String formattedDate = (String) method.invoke(employeeService, timestamp);
        assertEquals("2021-10-01", formattedDate);
    }

    @Test
    void testFormatTimestamp_InvalidTimestamp() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("formatTimestamp", String.class);
        method.setAccessible(true);
        String timestamp = "invalid";
        String formattedDate = (String) method.invoke(employeeService, timestamp);
        assertEquals(UNKNOWN_DATE_FORMAT, formattedDate);
    }

    @Test
    void testConvertDaysToDate_ValidDays() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("convertDaysToDate", String.class);
        method.setAccessible(true);
        String days = "18628"; // Example days since epoch
        String formattedDate = (String) method.invoke(employeeService, days);
        assertEquals("2021-01-01", formattedDate);
    }

    @Test
    void testConvertDaysToDate_InvalidDays() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("convertDaysToDate", String.class);
        method.setAccessible(true);
        String days = "invalid";
        String formattedDate = (String) method.invoke(employeeService, days);
        assertEquals(UNKNOWN_DATE_FORMAT, formattedDate);
    }
    @Test
    void testSetActivityStatus() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("setActivityStatus", ApiProfileResponse.Result.class, EmployeeDTO.class);
        method.setAccessible(true);

        employeeDTO.setIsActiveStepUser(null);
        result.setEntity(new ApiProfileResponse.Result.Entity());
        result.getEntity().setActive(true);

        method.invoke(employeeService, result, employeeDTO);

        assertEquals(USER_STATUS_ACTIVE, employeeDTO.getIsActive());

    }

    @Test
    void testSetPromotionDate() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("setPromotionDate", ApiProfileResponse.Result.class, EmployeeDTO.class);
        method.setAccessible(true);

        result.setJobFunctionEffectiveFrom("18628");

        method.invoke(employeeService, result, employeeDTO);

        assertEquals("2021-01-01", employeeDTO.getLastPromotionDate());
    }

    @Test
    void testFetchAssessmentRecords() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("fetchAssessmentRecords", UserProfile.class, EmployeeDTO.class);
        method.setAccessible(true);

        AssessmentDetailsResponse response = new AssessmentDetailsResponse();
        AssessmentDetailsResponse.Result assessmentResult = new AssessmentDetailsResponse.Result();
        assessmentResult.setSessionDateTime("1633046400000");
        assessmentResult.setStatus("Closed");
        assessmentResult.setAssessmentResult("Passed");
        response.setResults(List.of(assessmentResult));

        when(userProfileService.fetchUserAssessmentRequests(userProfile.getEmploymentId())).thenReturn(response);

        method.invoke(employeeService, userProfile, employeeDTO);

        assertEquals("2021-10-01", employeeDTO.getLastAssessmentDate());
        assertEquals("Passed", employeeDTO.getLastAssessmentResult());
    }

    @Test
    void testFetchBenchHistory() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("fetchBenchHistory", UserProfile.class, EmployeeDTO.class);
        method.setAccessible(true);

        BenchHistoryResponse response = new BenchHistoryResponse();
        BenchHistoryResponse.Result benchResult = new BenchHistoryResponse.Result();
        BenchHistoryResponse.Result.Entity entity = new BenchHistoryResponse.Result.Entity();
        BenchHistoryResponse.Result.BenchRecord benchRecord = new BenchHistoryResponse.Result.BenchRecord();
        benchRecord.setStatus("Active");
        entity.setBenchRecords(List.of(benchRecord));
        benchResult.setEntity(entity);
        response.setResults(List.of(benchResult));

        when(userProfileService.fetchBenchHistory(userProfile.getEmploymentId())).thenReturn(response);

        method.invoke(employeeService, userProfile, employeeDTO);

        assertEquals("Active", employeeDTO.getBenchStatus());
    }
    @Test
    void testCreateEmployeeDTO_UserNotFound() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("createEmployeeDTO", String.class, ApiProfileResponse.Result.class, UserProfile.class);
        method.setAccessible(true);
        when(userService.getUser(validEmail)).thenThrow(new UserNotFoundException("User not found for email: " + validEmail));
        when(topTalentEmployeeRepository.findByEmail(validEmail)).thenReturn(Optional.of(topTalentEmployee));
        when(employeeMapper.toEmployeeProfileDTO(userProfile, topTalentEmployee, UserDTO.builder().isActive(USER_STATUS_INACTIVE).build())).thenReturn(employeeDTO);

        EmployeeDTO resultDTO = (EmployeeDTO) method.invoke(employeeService, validEmail, result, userProfile);

        assertNotNull(resultDTO);
        verify(userService, times(1)).getUser(validEmail);
        verify(topTalentEmployeeRepository, times(1)).findByEmail(validEmail);
        verify(employeeMapper, times(1)).toEmployeeProfileDTO(userProfile, topTalentEmployee, UserDTO.builder().isActive(USER_STATUS_INACTIVE).build());
    }

    @Test
    void testSetPromotionDate_NullJobFunctionEffectiveFrom() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("setPromotionDate", ApiProfileResponse.Result.class, EmployeeDTO.class);
        method.setAccessible(true);
        result.setJobFunctionEffectiveFrom(null);

        method.invoke(employeeService, result, employeeDTO);

        assertEquals(NOT_AVAIALABLE, employeeDTO.getLastPromotionDate());
    }

    @Test
    void testSetPromotionDate_EmptyJobFunctionEffectiveFrom() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("setPromotionDate", ApiProfileResponse.Result.class, EmployeeDTO.class);
        method.setAccessible(true);
        result.setJobFunctionEffectiveFrom("");

        method.invoke(employeeService, result, employeeDTO);

        assertEquals(NOT_AVAIALABLE, employeeDTO.getLastPromotionDate());
    }

    @Test
    void testFormatTimestamp_NullTimestamp() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("formatTimestamp", String.class);
        method.setAccessible(true);
        String nullTimestamp = null;

        String result = (String) method.invoke(employeeService, nullTimestamp);

        assertEquals(NOT_AVAIALABLE, result);
    }

    @Test
    void testFormatTimestamp_EmptyTimestamp() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("formatTimestamp", String.class);
        method.setAccessible(true);
        String emptyTimestamp = "";

        String result = (String) method.invoke(employeeService, emptyTimestamp);

        assertEquals(NOT_AVAIALABLE, result);
    }
    @Test
    void testFetchAssessmentRecords_NullResponse() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("fetchAssessmentRecords", UserProfile.class, EmployeeDTO.class);
        method.setAccessible(true);

        when(userProfileService.fetchUserAssessmentRequests(userProfile.getEmploymentId())).thenReturn(null);

        method.invoke(employeeService, userProfile, employeeDTO);

        assertEquals(NOT_AVAIALABLE, employeeDTO.getLastAssessmentDate());
        assertEquals(NOT_AVAIALABLE, employeeDTO.getLastAssessmentResult());
    }

    @Test
    void testFetchAssessmentRecords_EmptyResults() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("fetchAssessmentRecords", UserProfile.class, EmployeeDTO.class);
        method.setAccessible(true);

        AssessmentDetailsResponse response = new AssessmentDetailsResponse();
        response.setResults(Collections.emptyList());

        when(userProfileService.fetchUserAssessmentRequests(userProfile.getEmploymentId())).thenReturn(response);

        method.invoke(employeeService, userProfile, employeeDTO);

        assertEquals(NOT_AVAIALABLE, employeeDTO.getLastAssessmentDate());
        assertEquals(NOT_AVAIALABLE, employeeDTO.getLastAssessmentResult());
    }
    @Test
    void testValidateEmail_NullEmail() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("validateEmail", String.class);
        method.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> method.invoke(employeeService, (String) null));
        assertEquals(MISSING_EMAIL, exception.getTargetException().getMessage());
    }

    @Test
    void testValidateEmail_EmptyEmail() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("validateEmail", String.class);
        method.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> method.invoke(employeeService, ""));
        assertEquals(MISSING_EMAIL, exception.getTargetException().getMessage());
    }

    @Test
    void testValidateEmail_InvalidEmail() throws Exception {
        Method method = EmployeeServiceImpl.class.getDeclaredMethod("validateEmail", String.class);
        method.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> method.invoke(employeeService, "invalid_email"));
        assertEquals(INVALID_EMAIL, exception.getTargetException().getMessage());
    }

}