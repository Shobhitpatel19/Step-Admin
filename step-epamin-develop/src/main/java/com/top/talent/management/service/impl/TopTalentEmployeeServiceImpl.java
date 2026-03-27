package com.top.talent.management.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.TalentProfileDTO;
import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.entity.VersionStatus;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.CorruptedFileException;
import com.top.talent.management.exception.InvalidFileFormatException;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.repository.VersionStatusRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.TopTalentExcelVersionRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.EmailService;
import com.top.talent.management.service.IdentificationClosureService;
import com.top.talent.management.service.TopTalentEmployeeService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.utils.ExcelRowListener;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.top.talent.management.constants.Constants.FILE_NAME;
import static com.top.talent.management.constants.Constants.STAR;
import static com.top.talent.management.constants.Constants.RISING_STAR;
import static com.top.talent.management.constants.Constants.SUPER_STAR;

import static com.top.talent.management.constants.NumericConstants.HEADER;
import static com.top.talent.management.utils.TopTalentEmployeeUtils.extractYear;
import static com.top.talent.management.utils.TopTalentEmployeeUtils.validateYear;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopTalentEmployeeServiceImpl implements TopTalentEmployeeService {

    private final TopTalentEmployeeRepository topTalentEmployeeRepository;
    private final TopTalentExcelVersionService topTalentExcelVersionService;
    private final TopTalentExcelVersionRepository topTalentExcelVersionRepository;
    private final TopTalentEmployeeMapper topTalentEmployeeMapper;
    private final VersionStatusRepository versionStatusRepository;

    private final IdentificationClosureService identificationClosureService;

    private final EmailService emailService;


    @Transactional
    public List<TopTalentEmployeeDTO> parseAndSaveExcel(MultipartFile file, CustomUserPrincipal customUserPrincipal) {
        if (!Objects.requireNonNull(file.getOriginalFilename()).matches(FILE_NAME)) {
            throw new InvalidFileFormatException(ErrorMessages.INVALID_FILE_FORMAT);
        }

        String fileYear = extractYear(file.getOriginalFilename());
        int currentYear = LocalDateTime.now().getYear();

        if (Boolean.FALSE.equals(validateYear(fileYear, currentYear))) {
            throw new VersionException(ErrorMessages.YEAR_MISMATCH);
        }

        if (topTalentExcelVersionService.checkIfFileExists(file.getOriginalFilename())) {
            throw new VersionException(ErrorMessages.VERSION_ALREADY_EXISTS);
        }

        ExcelRowListener listener = new ExcelRowListener(topTalentExcelVersionService, file.getOriginalFilename(), customUserPrincipal);

        try (InputStream fis = file.getInputStream()) {
            WorkbookFactory.create(fis);
            EasyExcelFactory.read(file.getInputStream(), TopTalentEmployee.class, listener)
                    .ignoreEmptyRow(true)
                    .headRowNumber(HEADER)
                    .sheet().doRead();
        } catch (IOException ioException) {
            log.error("Error reading file: {}", ioException.getMessage());
            throw new CorruptedFileException(ErrorMessages.CORRUPTED_EXCEL_FILE);
        }

        topTalentEmployeeRepository.saveAll(listener.getTopTalentEmployeeList());
        LocalDate date = LocalDate.now();
        TopTalentExcelVersion excelVersion = topTalentExcelVersionRepository.findByFileName(file.getOriginalFilename()).get();
        emailService.generateDatesAndTemplates(date, excelVersion);
        emailService.sendMailToPractices(date, excelVersion);

        TopTalentExcelVersion topTalentExcelVersion = topTalentExcelVersionService.findLatestVersion();

        List<TopTalentEmployee> topTalentEmployees = topTalentEmployeeRepository.findAllByTopTalentExcelVersion(topTalentExcelVersion);

        versionStatusRepository.save(VersionStatus.builder()
                .topTalentExcelVersion(topTalentExcelVersion)
                .submissionStatus(SubmissionStatus.NA)
                .created(LocalDateTime.now())
                .createdBy(customUserPrincipal.getFullName())
                .lastUpdated(LocalDateTime.now())
                .lastUpdatedBy(customUserPrincipal.getFullName())
                .build());

        return topTalentEmployees.stream()
                .map(topTalentEmployeeMapper::employeeDataToEmployeeDTO)
                .toList();
    }

    public List<TopTalentEmployee> getAllEmployeeDataByVersionAndYear(String year, String versionName) {
        List<TopTalentEmployee> employees = topTalentEmployeeRepository.findAllByTopTalentExcelVersionUploadedYearAndTopTalentExcelVersionVersionName(year, versionName);
        if(employees.isEmpty()) {
            return employees;
        }
        try {
            if (identificationClosureService.isPhaseClosed()) {
                return employees.stream()
                        .filter(TopTalentEmployee::getIsStepUser)
                        .sorted(Comparator.comparing(TopTalentEmployee::getUid))
                        .toList();
            }
        } catch (VersionException ignored) {
            log.error("Failed to check phase closure for file: {}. Error: {}", year+versionName, ignored.getMessage(), ignored);
        }
        return employees.stream()
                .sorted(Comparator.comparing(TopTalentEmployee::getUid))
                .toList();

    }

    public List<TopTalentEmployeeDTO> getAllEmployeeDataByYear() {
        String currentYear = String.valueOf(LocalDateTime.now().getYear());

        List<TopTalentExcelVersion> versions = topTalentExcelVersionRepository.findAllByUploadedYear(currentYear);
        if (versions.isEmpty()) {
            throw new VersionException(ErrorMessages.LIST_NOT_FOUND);
        }

        List<TopTalentEmployee> employees = versions.stream()
                .flatMap(version -> topTalentEmployeeRepository.findAllByTopTalentExcelVersion(version).stream())
                .toList();

        return employees.stream()
                .map(topTalentEmployeeMapper::employeeDataToEmployeeDTO)
                .toList();
    }

    public List<TopTalentEmployeeDTO> getAllEmployeeDataByLatestVersion() {
        TopTalentExcelVersion latestVersion = topTalentExcelVersionService.findLatestVersion();
        log.info("latestVersion:{}", latestVersion);
        List<TopTalentEmployee> employees = topTalentEmployeeRepository.findAllByTopTalentExcelVersion(latestVersion);
        try {
            boolean isphaseClosed = identificationClosureService.isPhaseClosed();
            if (isphaseClosed) {
                return employees.stream()
                        .filter(TopTalentEmployee::getIsStepUser)
                        .map(topTalentEmployeeMapper::employeeDataToEmployeeDTO)
                        .toList();
            }
        } catch (VersionException ignored) {
            log.error("Error while checking phase closure: {}", ignored.getMessage(), ignored);
            return employees.stream()
                    .map(topTalentEmployeeMapper::employeeDataToEmployeeDTO)
                    .toList();
        }
        return employees.stream()
                .map(topTalentEmployeeMapper::employeeDataToEmployeeDTO)
                .toList();
    }

    public List<TopTalentEmployeeDTO> getAllEmployeeDataByUid(Long uid) {
        return topTalentEmployeeRepository.findAllByUid(uid)
                .stream().map(topTalentEmployeeMapper::employeeDataToEmployeeDTO)
                .toList();
    }

    public List<TalentProfileDTO> getEmployeeDataForTalentProfile(Long uid) {
        List<TopTalentEmployee> employees = topTalentEmployeeRepository.findAllByUid(uid);
        return employees.stream()
                .map(topTalentEmployeeMapper::employeeToTalentProfileDTO)
                .toList();
    }

    public List<TopTalentEmployee> saveAll(List<TopTalentEmployee> topTalentEmployees) {
        return topTalentEmployeeRepository.saveAll(topTalentEmployees);
    }
}
