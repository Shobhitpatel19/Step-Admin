package com.top.talent.management.service;

import com.alibaba.excel.EasyExcel;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.TalentProfileDTO;
import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.repository.VersionStatusRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.TopTalentExcelVersionRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.TopTalentEmployeeServiceImpl;
import com.top.talent.management.utils.TestUtils;
import com.top.talent.management.utils.TopTalentEmployeeTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TopTalentEmployeeServiceTest {

    @Mock
    private TopTalentEmployeeRepository topTalentEmployeeRepository;

    @Mock
    private TopTalentExcelVersionService topTalentExcelVersionService;

    @Mock
    private TopTalentExcelVersionRepository topTalentExcelVersionRepository;

    @Mock
    private TopTalentEmployeeMapper topTalentEmployeeMapper;

    @Mock
    IdentificationClosureService identificationClosureService;

    @Mock
    private VersionStatusRepository versionStatusRepository;

    @Mock
    private EmailService emailService;


    @InjectMocks
    private TopTalentEmployeeServiceImpl topTalentEmployeeService;

    @Test
    void parseAndSave_success() throws Exception {
        String fileName = "STEP_2025_V1.xlsx";

        // Create a valid employee entity using the utility method
        TopTalentExcelVersion version = TopTalentEmployeeTestUtils.createVersion(fileName, "2025", "V1");
        TopTalentEmployee employee = TopTalentEmployeeTestUtils.createEmployee(123456L, version); // Create a valid employee

        MockMultipartFile file = TopTalentEmployeeTestUtils.createValidFile(fileName);

        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();

        when(topTalentExcelVersionService.checkIfFileExists(fileName)).thenReturn(false);
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(version);
        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(version)).thenReturn(List.of(employee));
        when(topTalentExcelVersionService.saveVersion(anyString(), any(CustomUserPrincipal.class), anyString(), anyString())).thenReturn(version);

        when(topTalentEmployeeRepository.saveAll(any())).thenReturn(List.of(employee));
        when(topTalentEmployeeMapper.employeeDataToEmployeeDTO(any())).thenReturn(new TopTalentEmployeeDTO());
        when(topTalentExcelVersionRepository.findByFileName(fileName)).thenReturn(Optional.of(version));

        List<TopTalentEmployeeDTO> result = topTalentEmployeeService.parseAndSaveExcel(file, customUserPrincipal);

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    void testGetEmployeeDataByVersionAndYear_Success() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String versionName = "V1";
        TopTalentExcelVersion version = TopTalentEmployeeTestUtils.createVersion("STEP_" + year + "_V1.xlsx", year, versionName);

        TopTalentEmployee employee = TopTalentEmployeeTestUtils.createEmployee(123456L, version);

        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersionUploadedYearAndTopTalentExcelVersionVersionName(year,"V1")).thenReturn(Collections.singletonList(employee));

        List<TopTalentEmployee> result = topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, versionName);

        assertEquals(1, result.size());
        verify(topTalentEmployeeRepository, times(1)).findAllByTopTalentExcelVersionUploadedYearAndTopTalentExcelVersionVersionName(year, versionName);
    }
    @Test
    void testGetEmployeeDataByVersionAndYear_Success_PhaseClosed() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String versionName = "V1";
        TopTalentExcelVersion version = TopTalentEmployeeTestUtils.createVersion("STEP_" + year + "_V1.xlsx", year, versionName);

        TopTalentEmployee employee = TopTalentEmployeeTestUtils.createEmployee(123456L, version);
        employee.setIsStepUser(true);

        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersionUploadedYearAndTopTalentExcelVersionVersionName(year,"V1")).thenReturn(Collections.singletonList(employee));

        when(identificationClosureService.isPhaseClosed()).thenReturn(true);
        List<TopTalentEmployee> result = topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, versionName);

        assertEquals(1, result.size());
        verify(topTalentEmployeeRepository, times(1)).findAllByTopTalentExcelVersionUploadedYearAndTopTalentExcelVersionVersionName(year, versionName);
    }

    @Test
    void testGetEmployeeDataByVersionAndYear_Success_empty() {
        String year = String.valueOf(LocalDateTime.now().getYear());

        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersionUploadedYearAndTopTalentExcelVersionVersionName(year,"V1")).thenReturn(Collections.emptyList());

        List<TopTalentEmployee> result = topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, "V1");

        assertEquals(0, result.size());
        verify(topTalentEmployeeRepository, times(1)).findAllByTopTalentExcelVersionUploadedYearAndTopTalentExcelVersionVersionName(year, "V1");
    }

    @Test
    void parseExcelFile_EmptyFileWithHeaders() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();

        EasyExcel.write(byteArrayOutputStream, TopTalentEmployee.class)
                .sheet("Sheet1")
                .doWrite(Collections.emptyList());

        MockMultipartFile file = new MockMultipartFile("file",
                "STEP_2020_V1.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                byteArrayOutputStream.toByteArray());

        TopTalentEmployeeServiceImpl realService = new TopTalentEmployeeServiceImpl(topTalentEmployeeRepository, topTalentExcelVersionService, topTalentExcelVersionRepository, topTalentEmployeeMapper, versionStatusRepository, identificationClosureService,emailService);


        assertThrows(VersionException.class, () -> realService.parseAndSaveExcel(file, customUserPrincipal));
    }

    @Test
    void parseExcelFile_EmptyFileWithOutHeaders() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();
        EasyExcel.write(byteArrayOutputStream).sheet("Sheet1").doWrite(() -> null);

        MockMultipartFile file = new MockMultipartFile("file",
                "STEP_2020_V1.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                byteArrayOutputStream.toByteArray());

        TopTalentEmployeeServiceImpl realService = new TopTalentEmployeeServiceImpl(topTalentEmployeeRepository, topTalentExcelVersionService, topTalentExcelVersionRepository, topTalentEmployeeMapper, versionStatusRepository, identificationClosureService, emailService);

        assertThrows(VersionException.class, () -> realService.parseAndSaveExcel(file, customUserPrincipal));
    }
    @Test
    void getAllEmployeeDataByYear_successful() {
        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        TopTalentExcelVersion version = TopTalentEmployeeTestUtils.createVersion("STEP_HEROES_" + currentYear + "_V1.xlsx", currentYear, "V1");

        TopTalentEmployee employee = TopTalentEmployeeTestUtils.createEmployee(1L, version);

        when(topTalentExcelVersionRepository.findAllByUploadedYear(currentYear)).thenReturn(Collections.singletonList(version));
        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(version)).thenReturn(Collections.singletonList(employee));

        List<TopTalentEmployeeDTO> result = topTalentEmployeeService.getAllEmployeeDataByYear();

        assertEquals(1, result.size());
        verify(topTalentEmployeeRepository, times(1)).findAllByTopTalentExcelVersion(version);
    }

    @Test
    void getAllEmployeeDataByYear_throwsVersionNotFoundException() {
        String currentYear = String.valueOf(LocalDateTime.now().getYear());

        when(topTalentExcelVersionRepository.findAllByUploadedYear(currentYear)).thenReturn(Collections.emptyList());

        VersionException exception = assertThrows(VersionException.class, () ->
                topTalentEmployeeService.getAllEmployeeDataByYear()
        );
        assertEquals(ErrorMessages.LIST_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetEmployeeDataForTalentProfile() {
        Long uid = 1L;
        TopTalentEmployee employee = TopTalentEmployeeTestUtils.createEmployee(uid, null);

        when(topTalentEmployeeRepository.findAllByUid(uid)).thenReturn(Collections.singletonList(employee));

        List<TalentProfileDTO> result = topTalentEmployeeService.getEmployeeDataForTalentProfile(uid);

        assertEquals(1, result.size());
        verify(topTalentEmployeeRepository, times(1)).findAllByUid(uid);
    }

    @Test
    void getAllEmployeeDataByUid_validUid_returnsDTOList() {
        Long uid = 1L;
        TopTalentEmployee employee = TopTalentEmployeeTestUtils.createEmployee(uid, null);

        when(topTalentEmployeeRepository.findAllByUid(uid)).thenReturn(Collections.singletonList(employee));

        List<TopTalentEmployeeDTO> result = topTalentEmployeeService.getAllEmployeeDataByUid(uid);

        assertEquals(1, result.size());
        verify(topTalentEmployeeRepository, times(1)).findAllByUid(uid);
    }

    @Test
    void testGetAllEmployeeData_WithLatestVersion() {
        // Given: A mock latest version and list of employees
        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        latestVersion.setVersionName("V1");
        latestVersion.setUploadedYear("2024");

        TopTalentEmployee employee1 = new TopTalentEmployee();
        employee1.setUid(123L);
        TopTalentEmployee employee2 = new TopTalentEmployee();
        employee2.setUid(456L);

        List<TopTalentEmployee> employees = Arrays.asList(employee1, employee2);

        // Mocking services
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(latestVersion)).thenReturn(employees);

        TopTalentEmployeeDTO dto1 = new TopTalentEmployeeDTO();
        TopTalentEmployeeDTO dto2 = new TopTalentEmployeeDTO();

        when(topTalentEmployeeMapper.employeeDataToEmployeeDTO(employee1)).thenReturn(dto1);
        when(topTalentEmployeeMapper.employeeDataToEmployeeDTO(employee2)).thenReturn(dto2);

        // When: Calling the method
        List<TopTalentEmployeeDTO> result = topTalentEmployeeService.getAllEmployeeDataByLatestVersion();

        // Then: Verify that the method returns a list of mapped employee DTOs
        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));
        assertSame(dto2, result.get(1));

        // Verify interactions
        verify(topTalentExcelVersionService).findLatestVersion();
        verify(topTalentEmployeeRepository).findAllByTopTalentExcelVersion(latestVersion);
        verify(topTalentEmployeeMapper).employeeDataToEmployeeDTO(employee1);
        verify(topTalentEmployeeMapper).employeeDataToEmployeeDTO(employee2);
    }

    @Test
    void testGetAllEmployeeData_WithLatestVersion_IDPhaseClosed() {
        // Given: A mock latest version and list of employees
        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        latestVersion.setVersionName("V1");
        latestVersion.setUploadedYear("2024");

        TopTalentEmployee employee1 = new TopTalentEmployee();
        employee1.setUid(123L);
        employee1.setIsStepUser(true);
        TopTalentEmployee employee2 = new TopTalentEmployee();
        employee2.setUid(456L);
        employee2.setIsStepUser(false);

        List<TopTalentEmployee> employees = Arrays.asList(employee1, employee2);

        // Mocking services
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(latestVersion)).thenReturn(employees);

        TopTalentEmployeeDTO dto1 = new TopTalentEmployeeDTO();


        when(topTalentEmployeeMapper.employeeDataToEmployeeDTO(employee1)).thenReturn(dto1);

        when(identificationClosureService.isPhaseClosed()).thenReturn(true);
        // When: Calling the method
        List<TopTalentEmployeeDTO> result = topTalentEmployeeService.getAllEmployeeDataByLatestVersion();

        // Then: Verify that the method returns a list of mapped employee DTOs
        assertEquals(1, result.size());
        assertSame(dto1, result.get(0));

        // Verify interactions
        verify(topTalentExcelVersionService).findLatestVersion();
        verify(topTalentEmployeeRepository).findAllByTopTalentExcelVersion(latestVersion);
        verify(topTalentEmployeeMapper).employeeDataToEmployeeDTO(employee1);
    }
    @Test
    void testGetAllEmployeeData_WithLatestVersion_NoPhase() {
        // Given: A mock latest version and list of employees
        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion();
        latestVersion.setVersionName("V1");
        latestVersion.setUploadedYear("2024");

        TopTalentEmployee employee1 = new TopTalentEmployee();
        employee1.setUid(123L);
        employee1.setIsStepUser(true);
        TopTalentEmployee employee2 = new TopTalentEmployee();
        employee2.setUid(456L);
        employee2.setIsStepUser(false);

        List<TopTalentEmployee> employees = Arrays.asList(employee1, employee2);

        // Mocking services
        when(topTalentExcelVersionService.findLatestVersion()).thenReturn(latestVersion);
        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(latestVersion)).thenReturn(employees);

        TopTalentEmployeeDTO dto1 = new TopTalentEmployeeDTO();


        when(topTalentEmployeeMapper.employeeDataToEmployeeDTO(employee1)).thenReturn(dto1);

        when(identificationClosureService.isPhaseClosed()).thenThrow(new VersionException(ErrorMessages.NO_PHASES_FOUND));
        // When: Calling the method
        List<TopTalentEmployeeDTO> result = topTalentEmployeeService.getAllEmployeeDataByLatestVersion();

        // Then: Verify that the method returns a list of mapped employee DTOs
        assertEquals(2, result.size());
        assertSame(dto1, result.get(0));

        // Verify interactions
        verify(topTalentExcelVersionService).findLatestVersion();
        verify(topTalentEmployeeRepository).findAllByTopTalentExcelVersion(latestVersion);
        verify(topTalentEmployeeMapper).employeeDataToEmployeeDTO(employee1);
    }

    @Test
    void testSaveAll(){
        TopTalentEmployee employee = new TopTalentEmployee();
        employee.setUid(123L);
        List<TopTalentEmployee> employees = List.of(employee);
        when(topTalentEmployeeRepository.saveAll(employees)).thenReturn(employees);
        List<TopTalentEmployee> result = topTalentEmployeeService.saveAll(employees);
        assertEquals(1, result.size());
    }

}

