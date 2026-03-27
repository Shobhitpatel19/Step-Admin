package com.top.talent.management.service.impl;

import com.alibaba.excel.exception.ExcelAnalysisException;
import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.dto.CulturalScore;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.exception.CorruptedFileException;
import com.top.talent.management.exception.CultureScoreException;
import com.top.talent.management.exception.InvalidFileFormatException;
import com.top.talent.management.service.CulturalScoreService;
import com.top.talent.management.service.TopTalentEmployeeService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.utils.CulturalScoreListener;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.security.CustomUserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.alibaba.excel.EasyExcelFactory.read;
import static com.top.talent.management.constants.Constants.CULTURAL_SCORE_FILE_NAME;
import static com.top.talent.management.constants.NumericConstants.HEADER;
import static com.top.talent.management.utils.CultureScoreUtils.extractVersionName;
import static com.top.talent.management.utils.CultureScoreUtils.extractYear;

@Slf4j
@RequiredArgsConstructor
@Service
public class CulturalScoreServiceImpl implements CulturalScoreService {

    private final TopTalentExcelVersionService talentExcelVersionService;
    private final TopTalentEmployeeMapper dataMapper;

    private final TopTalentEmployeeService topTalentEmployeeService;

    @Transactional
    public List<TopTalentEmployeeDTO> parseAndSaveCulturalScore(MultipartFile file, CustomUserPrincipal customUserPrincipal) {
        if (!Objects.requireNonNull(file.getOriginalFilename()).matches(CULTURAL_SCORE_FILE_NAME))
            throw new InvalidFileFormatException(ErrorMessages.INVALID_FILE_FORMAT);

        String year = extractYear(file.getOriginalFilename());
        log.info(year);
        String versionName = extractVersionName(file.getOriginalFilename());
        log.info(versionName);

        if (!year.equals(String.valueOf(LocalDateTime.now().getYear()))) {
            throw new CultureScoreException(ErrorMessages.NOT_CURRENT_YEAR);
        }

        if (topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, versionName).isEmpty()) {
            throw new CultureScoreException(ErrorMessages.LIST_NOT_FOUND);
        }

        validateHeaders(file);
        CulturalScoreListener listener = new CulturalScoreListener(file.getOriginalFilename(), customUserPrincipal, talentExcelVersionService);

        try (InputStream inputStream = file.getInputStream()) {
            WorkbookFactory.create(inputStream);
            read(file.getInputStream(), CulturalScore.class, listener)
                    .ignoreEmptyRow(true).headRowNumber(HEADER)
                    .sheet().doRead();
        } catch (ExcelAnalysisException | IOException e) {
            log.error("Error parsing Excel file: {}", e.getMessage());
            throw new CorruptedFileException(ErrorMessages.CORRUPTED_EXCEL_FILE);
        }

        List<TopTalentEmployee> existingEmployeesList = topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, versionName);

        log.info("Existing Employees List: {}", existingEmployeesList);

        List<CulturalScore> culturalScoreEmployees = listener.getDataList().stream()
                .sorted(Comparator.comparingLong(CulturalScore::getUid))
                .toList();
        log.info("Cultural Scores: {}", culturalScoreEmployees);

        if (existingEmployeesList.size() != culturalScoreEmployees.size()) {
            throw new CultureScoreException(ErrorMessages.CULTURE_SCORE_SIZE_MISMATCH);
        }

        for (int i = 0; i < existingEmployeesList.size(); i++) {
            TopTalentEmployee masterExcelEmployee = existingEmployeesList.get(i);
            CulturalScore culturalScore = culturalScoreEmployees.get(i);

            if (!masterExcelEmployee.getUid().equals(culturalScore.getUid())) {
                throw new CultureScoreException(ErrorMessages.CULTURE_SCORE_UID_MISMATCH);
            }

            log.info("Setting Culture Score for UID {}: {}", masterExcelEmployee.getUid(), culturalScore.getCultureScoreFromFeedback());
            masterExcelEmployee.setCultureScoreFromFeedback(culturalScore.getCultureScoreFromFeedback());
        }

        log.info("saving...");
        topTalentEmployeeService.saveAll(existingEmployeesList);

        return existingEmployeesList.stream()
                .map(dataMapper::employeeDataToEmployeeDTO)
                .toList();
    }

    private void validateHeaders(MultipartFile file) {
        try (InputStream fis = file.getInputStream()) {
            WorkbookFactory.create(fis);
            read(file.getInputStream())
                    .headRowNumber(0)
                    .sheet()
                    .doReadSync()
                    .stream()
                    .findFirst()
                    .ifPresent(headerRow -> {
                        if (headerRow instanceof LinkedHashMap<?, ?>) {
                            Set<String> headers = ((LinkedHashMap<?, ?>) headerRow).values().stream()
                                    .map(Object::toString)
                                    .collect(Collectors.toSet());

                            if (!headers.containsAll(Constants.CULTURE_SCORE_REQUIRED_HEADERS)) {
                                throw new CultureScoreException(ErrorMessages.INVALID_HEADER);
                            }
                        }
                    });
        } catch (IOException e) {
            log.error("Failed to process the Excel file: {}", e.getMessage(), e);
            throw new CorruptedFileException(ErrorMessages.CORRUPTED_EXCEL_FILE);
        }

    }
}