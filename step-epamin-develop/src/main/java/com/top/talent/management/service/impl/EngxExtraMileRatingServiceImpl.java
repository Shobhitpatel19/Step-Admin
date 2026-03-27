package com.top.talent.management.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.dto.EngXExtraMileRating;
import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.exception.CorruptedFileException;
import com.top.talent.management.exception.EngXExtraMileRatingException;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.EngXExtraMileRatingService;
import com.top.talent.management.service.TopTalentEmployeeService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.utils.EngXExtraMileRowListener;
import com.top.talent.management.utils.EngxExtraMileUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.alibaba.excel.EasyExcelFactory.read;
import static com.top.talent.management.constants.NumericConstants.HEADER;
import static com.top.talent.management.utils.EngxExtraMileUtils.extractVersionName;
import static com.top.talent.management.utils.EngxExtraMileUtils.extractYear;
import static com.top.talent.management.utils.EngxExtraMileUtils.validateFileAndFileName;

@Service
@RequiredArgsConstructor
@Slf4j
public class EngxExtraMileRatingServiceImpl implements EngXExtraMileRatingService {

    private final TopTalentEmployeeMapper topTalentEmployeeMapper;

    private final TopTalentExcelVersionService topTalentExcelVersionService;

    private final TopTalentEmployeeService topTalentEmployeeService;

    @Override
    @Transactional
    public List<TopTalentEmployeeDTO> parseAndSaveExcel(MultipartFile file, CustomUserPrincipal customUserPrincipal) {
        validateFile(file);

        validateHeaders(file);

        EngXExtraMileRowListener listener = new EngXExtraMileRowListener(file.getOriginalFilename(), customUserPrincipal, topTalentExcelVersionService);

        parseFile(file,listener);

        List<TopTalentEmployee> topTalentEmployees = saveEngxExtraMileRatings(Objects.requireNonNull(file.getOriginalFilename()), listener);

        return topTalentEmployees.stream()
                .map(topTalentEmployeeMapper::employeeDataToEmployeeDTO)
                .toList();
    }

    private void validateFile(MultipartFile file) {
        validateFileAndFileName(file);
        String versionName = extractVersionName(Objects.requireNonNull(file.getOriginalFilename()));
        String year = extractYear(file.getOriginalFilename());

        if (topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, versionName).isEmpty()) {
            throw new VersionException(ErrorMessages.LIST_NOT_FOUND);
        }

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

                            if (!headers.containsAll(Constants.HEROES_REQUIRED_HEADERS)) {
                                throw new EngXExtraMileRatingException(ErrorMessages.INVALID_HEADER);
                            }
                        }
                    });
        } catch (IOException e) {
            log.error("Failed to validate headers for file: {}. Error: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new CorruptedFileException(ErrorMessages.CORRUPTED_EXCEL_FILE);
        }

    }

    private void parseFile(MultipartFile file,EngXExtraMileRowListener listener){
        try (InputStream fis = file.getInputStream()) {
            WorkbookFactory.create(fis);
            EasyExcelFactory.read(file.getInputStream(), EngXExtraMileRating.class, listener)
                    .ignoreEmptyRow(true)
                    .headRowNumber(HEADER)
                    .sheet().doRead();
        } catch (IOException ioException) {
            log.error("Error reading file: {}", ioException.getMessage());
            throw new CorruptedFileException(ErrorMessages.CORRUPTED_EXCEL_FILE);
        }
    }

    private List<TopTalentEmployee> saveEngxExtraMileRatings(String fileName, EngXExtraMileRowListener listener) {

        String year = EngxExtraMileUtils.extractYear(fileName);
        String versionName = EngxExtraMileUtils.extractVersionName(fileName);

        List<TopTalentEmployee> topTalentEmployees = topTalentEmployeeService.getAllEmployeeDataByVersionAndYear(year, versionName);

        List<EngXExtraMileRating> engXExtraMileRatingEmployees = listener.getRatingList().stream()
                .sorted(Comparator.comparingLong(EngXExtraMileRating::getUid))
                .toList();

        if (topTalentEmployees.size() != engXExtraMileRatingEmployees.size()) {
            throw new EngXExtraMileRatingException(ErrorMessages.ENGX_EXTRA_MILE_SIZE_MISMATCH);
        }

        for (int i = 0; i < topTalentEmployees.size(); i++) {
            TopTalentEmployee topTalentEmployee = topTalentEmployees.get(i);
            EngXExtraMileRating engXExtraMileRating = engXExtraMileRatingEmployees.get(i);

            if (!topTalentEmployee.getUid().equals(engXExtraMileRating.getUid())) {
                throw new EngXExtraMileRatingException(ErrorMessages.ENGX_EXTRA_MILE_UID_MISMATCH);
            }

            topTalentEmployee.setContributionEngXCulture(engXExtraMileRating.getEngXRating());
            topTalentEmployee.setContributionExtraMiles(engXExtraMileRating.getExtraMileRating());
        }

        return topTalentEmployeeService.saveAll(topTalentEmployees);
    }


}
