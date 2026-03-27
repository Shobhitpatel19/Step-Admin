package com.top.talent.management.service;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.CorruptedFileException;
import com.top.talent.management.exception.EngXExtraMileRatingException;
import com.top.talent.management.exception.InvalidFileFormatException;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.EngxExtraMileRatingServiceImpl;
import com.top.talent.management.service.impl.TopTalentEmployeeServiceImpl;
import com.top.talent.management.service.impl.TopTalentExcelVersionServiceImpl;
import com.top.talent.management.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.top.talent.management.utils.EngXExtraMileTestUtils.createEmployee;
import static com.top.talent.management.utils.EngXExtraMileTestUtils.createEmptyFile;
import static com.top.talent.management.utils.EngXExtraMileTestUtils.createFileWithInvalidHeaders;
import static com.top.talent.management.utils.EngXExtraMileTestUtils.createValidFile;
import static com.top.talent.management.utils.EngXExtraMileTestUtils.createVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EngXExtraMileRatingServiceTest {
    

    @Mock
    private TopTalentExcelVersionServiceImpl topTalentExcelVersionService;

    @Mock
    private TopTalentEmployeeMapper topTalentEmployeeMapper;
    

    @InjectMocks
    private EngxExtraMileRatingServiceImpl engXExtraMileRatingService;

    @Mock
    private TopTalentEmployeeServiceImpl topTalentEmployeeService;
    private String fileName;

    @Test
    void parseAndSaveRatingValidFileSuccess() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        fileName = "STEP_HEROES_"+year+"_V1.xlsx";
        MockMultipartFile file = createValidFile(fileName);
        TopTalentExcelVersion version = createVersion(fileName, year, "V1");
        TopTalentEmployee employeeWithoutEngX = createEmployee(123456L, null, null);
        TopTalentEmployee employeeWithEngX = createEmployee(234561L, 4L, 4L);

        mockRepositoryForValidFile(fileName, employeeWithoutEngX);

        Authentication authentication = TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN);
        when(topTalentExcelVersionService.saveVersion(fileName, (CustomUserPrincipal) authentication.getPrincipal(), "V1", year))
                .thenReturn(version);
        when(topTalentEmployeeService.saveAll(anyList())).thenReturn(Collections.singletonList(employeeWithEngX));

        List<?> result = engXExtraMileRatingService.parseAndSaveExcel(file, (CustomUserPrincipal) authentication.getPrincipal());

        assertEquals(1, result.size());
        assertEquals(4L, employeeWithoutEngX.getContributionExtraMiles());
        verify(topTalentEmployeeService, times(1)).saveAll(anyList());

    }

    @Test
    void parseAndSaveRatingValidFileSuccessPhaseEnded() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        fileName = "STEP_HEROES_"+year+"_V1.xlsx";
        MockMultipartFile file = createValidFile(fileName);
        TopTalentExcelVersion version = createVersion(fileName, year, "V1");
        TopTalentEmployee employeeWithoutEngX = createEmployee(123456L, null, null);
        employeeWithoutEngX.setIsStepUser(true);
        TopTalentEmployee employeeWithEngX = createEmployee(234561L, 4L, 4L);
        employeeWithEngX.setIsStepUser(true);

        mockRepositoryForValidFile(fileName, employeeWithoutEngX);

        Authentication authentication = TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN);
        when(topTalentExcelVersionService.saveVersion(fileName, (CustomUserPrincipal) authentication.getPrincipal(), "V1", year))
                .thenReturn(version);
        when(topTalentEmployeeService.saveAll(anyList())).thenReturn(Collections.singletonList(employeeWithEngX));

        List<?> result = engXExtraMileRatingService.parseAndSaveExcel(file, (CustomUserPrincipal) authentication.getPrincipal());

        assertEquals(1, result.size());
        assertEquals(4L, employeeWithoutEngX.getContributionExtraMiles());
        verify(topTalentEmployeeService, times(1)).saveAll(anyList());

    }
    @Test
    void parseAndSaveRatingValidFileSuccessNoPhase() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        fileName = "STEP_HEROES_"+year+"_V1.xlsx";
        MockMultipartFile file = createValidFile(fileName);
        TopTalentExcelVersion version = createVersion(fileName, year, "V1");
        TopTalentEmployee employeeWithoutEngX = createEmployee(123456L, null, null);
        employeeWithoutEngX.setIsStepUser(true);
        TopTalentEmployee employeeWithEngX = createEmployee(234561L, 4L, 4L);
        employeeWithEngX.setIsStepUser(true);

        mockRepositoryForValidFile(fileName, employeeWithoutEngX);

        Authentication authentication = TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN);
        when(topTalentExcelVersionService.saveVersion(fileName, (CustomUserPrincipal) authentication.getPrincipal(), "V1", year))
                .thenReturn(version);
        when(topTalentEmployeeService.saveAll(anyList())).thenReturn(Collections.singletonList(employeeWithEngX));

        List<?> result = engXExtraMileRatingService.parseAndSaveExcel(file, (CustomUserPrincipal) authentication.getPrincipal());

        assertEquals(1, result.size());
        assertEquals(4L, employeeWithoutEngX.getContributionExtraMiles());
        verify(topTalentEmployeeService, times(1)).saveAll(anyList());

    }
    @Test
    void parseAndSaveExcelThrowsCorruptedFileExceptionWhenFileIsEmpty() {
        MockMultipartFile emptyFile = createEmptyFile("empty.xlsx");
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();
        CorruptedFileException exception = assertThrows(CorruptedFileException.class, () ->
                engXExtraMileRatingService.parseAndSaveExcel(emptyFile, customUserPrincipal)
        );
        assertEquals(ErrorMessages.EMPTY_FILE, exception.getMessage());
    }

    @Test
    void parseAndSaveExcelThrowsInvalidFileFormatExceptionWhenFileNameIsInvalid() throws IOException {
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();
        MockMultipartFile invalidFile = createValidFile("invalid_file.xlsx");

        InvalidFileFormatException exception = assertThrows(InvalidFileFormatException.class, () ->
                engXExtraMileRatingService.parseAndSaveExcel(invalidFile, customUserPrincipal)
        );
        assertEquals(ErrorMessages.INVALID_FILE_FORMAT, exception.getMessage());
    }

    @Test
    void parseAndSaveExcelThrowsSizeMismatch() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        fileName = "STEP_HEROES_"+year+"_V1.xlsx";
        MockMultipartFile file = createValidFile(fileName);
        TopTalentExcelVersion version = createVersion(fileName, year, "V1");

        List<TopTalentEmployee> employees = List.of(
                createEmployee(123456L, null, null),
                createEmployee(234567L, null, null)
        );
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();
        mockRepositoryForValidFile(fileName, employees);
        when(topTalentExcelVersionService.saveVersion(fileName, customUserPrincipal, "V1", year))
                .thenReturn(version);

        EngXExtraMileRatingException exception = assertThrows(EngXExtraMileRatingException.class, () ->
                engXExtraMileRatingService.parseAndSaveExcel(file, customUserPrincipal)
        );
        assertEquals(ErrorMessages.ENGX_EXTRA_MILE_SIZE_MISMATCH, exception.getMessage());
    }

    @Test
    void parseAndSaveExcelThrowsUIDMismatch() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        fileName = "STEP_HEROES_"+year+"_V1.xlsx";
        MockMultipartFile file = createValidFile(fileName);
        TopTalentExcelVersion version = createVersion(fileName, year, "V1");
        TopTalentEmployee employeeWithoutEngX = createEmployee(123356L, null, null);
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();

        mockRepositoryForValidFile(fileName, employeeWithoutEngX);
        when(topTalentExcelVersionService.saveVersion(fileName, customUserPrincipal, "V1", year))
                .thenReturn(version);



        EngXExtraMileRatingException exception = assertThrows(EngXExtraMileRatingException.class, () ->
                engXExtraMileRatingService.parseAndSaveExcel(file, customUserPrincipal)
        );
        assertEquals(ErrorMessages.ENGX_EXTRA_MILE_UID_MISMATCH, exception.getMessage());

    }

    @Test
    void parseAndSaveExcelThrowsDataNotFound() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        fileName = "STEP_HEROES_"+year+"_V3.xlsx";
        MockMultipartFile file = createValidFile(fileName);
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();

        when(topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, "V3"))
                .thenReturn(Collections.emptyList());

        VersionException exception = assertThrows(VersionException.class, () ->
                engXExtraMileRatingService.parseAndSaveExcel(file, customUserPrincipal)
        );
        assertEquals(ErrorMessages.LIST_NOT_FOUND, exception.getMessage());
    }

    @Test
    void parseAndSaveExcelThrowsYearMismatch() throws IOException {
        fileName = "STEP_HEROES_2024_V1.xlsx";
        MockMultipartFile file = createValidFile(fileName);
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();

        VersionException exception = assertThrows(VersionException.class, () ->
                engXExtraMileRatingService.parseAndSaveExcel(file, customUserPrincipal)
        );
        assertEquals(ErrorMessages.YEAR_MISMATCH, exception.getMessage());
    }

    @Test
    void parseAndSaveExcelInvalidFileHeaders() throws IOException {
        String year = String.valueOf(LocalDateTime.now().getYear());
        fileName = "STEP_HEROES_"+year+"_V1.xlsx";
        MockMultipartFile invalidFile = createFileWithInvalidHeaders(fileName);
        CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) TestUtils.getMockAuthentication(RoleConstants.ROLE_SUPER_ADMIN).getPrincipal();
        
        when(topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(any(), any()))
                .thenReturn(List.of(new TopTalentEmployee()));
        EngXExtraMileRatingException exception = assertThrows(EngXExtraMileRatingException.class, () ->
                engXExtraMileRatingService.parseAndSaveExcel(
                        invalidFile,
                        customUserPrincipal)
        );
        assertEquals(ErrorMessages.INVALID_HEADER, exception.getMessage());
    }


    private void mockRepositoryForValidFile(String fileName, TopTalentEmployee employee) {
        mockRepositoryForValidFile(fileName, List.of(employee));
    }

    private void mockRepositoryForValidFile(String fileName, List<TopTalentEmployee> employees) {
        this.fileName = fileName;
        when(topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(String.valueOf(LocalDateTime.now().getYear()), "V1"))
                .thenReturn(employees);
    }
}
