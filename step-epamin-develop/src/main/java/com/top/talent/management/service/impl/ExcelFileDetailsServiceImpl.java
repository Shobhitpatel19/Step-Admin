package com.top.talent.management.service.impl;

import com.top.talent.management.dto.ExcelFileDetailsDTO;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.service.ExcelFileDetailsService;
import com.top.talent.management.service.TopTalentExcelVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.top.talent.management.constants.Constants.CULTURAL_SCORE;
import static com.top.talent.management.constants.Constants.CULTURAL_SCORE_SUFFIX;
import static com.top.talent.management.constants.Constants.CULTURE_SCORE_REQUIRED_HEADERS;
import static com.top.talent.management.constants.Constants.HEROES;
import static com.top.talent.management.constants.Constants.HEROES_SUFFIX;
import static com.top.talent.management.constants.Constants.HEROES_REQUIRED_HEADERS;
import static com.top.talent.management.constants.Constants.MANDATORY_COLUMNS;
import static com.top.talent.management.constants.Constants.REQUIRED_COLUMNS;
import static com.top.talent.management.constants.Constants.STEP;
import static com.top.talent.management.constants.Constants.STEP_SUFFIX;
import static com.top.talent.management.constants.Constants.UNDERSCORE;
import static com.top.talent.management.constants.Constants.VERSION_1_PREFIX;
import static com.top.talent.management.constants.Constants.VERSION_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelFileDetailsServiceImpl implements ExcelFileDetailsService {

    private final TopTalentExcelVersionService topTalentExcelVersionService;

    @Override
    public ExcelFileDetailsDTO generateExcelFile(String excelType){
        ExcelFileDetailsDTO excelFileDetailsDTO = new ExcelFileDetailsDTO();
        switch (excelType){
            case STEP:
                excelFileDetailsDTO.setExcelName(createFileNameWithVersionPrefix(STEP_SUFFIX,true));
                excelFileDetailsDTO.setHeaders(REQUIRED_COLUMNS);
                excelFileDetailsDTO.setMandatoryColumns(MANDATORY_COLUMNS);
                break;
            case HEROES:
                excelFileDetailsDTO.setExcelName(createFileNameWithVersionPrefix(STEP_SUFFIX +
                        HEROES_SUFFIX,false));
                excelFileDetailsDTO.setHeaders(HEROES_REQUIRED_HEADERS);
                excelFileDetailsDTO.setMandatoryColumns(HEROES_REQUIRED_HEADERS);
                break;
            case CULTURAL_SCORE:
                excelFileDetailsDTO.setExcelName(createFileNameWithVersionPrefix(STEP_SUFFIX +
                        CULTURAL_SCORE_SUFFIX,false));
                excelFileDetailsDTO.setHeaders(CULTURE_SCORE_REQUIRED_HEADERS);
                excelFileDetailsDTO.setMandatoryColumns(CULTURE_SCORE_REQUIRED_HEADERS);
                break;
            default:
                throw new IllegalArgumentException("Invalid Excel Type");
        }
        return excelFileDetailsDTO;
    }

    private String createFileNameWithVersionPrefix(String basePrefix, boolean incrementVersion) {
        TopTalentExcelVersion excelVersion;

        try {
            excelVersion = topTalentExcelVersionService.findLatestVersion();
        } catch (VersionException e) {
            log.info(e.getMessage(),e);
            return basePrefix + LocalDateTime.now().getYear() + VERSION_1_PREFIX;
        }

        String previousExcelVersion = excelVersion.getVersionName();
        log.info("Previous Version: {}", previousExcelVersion);
        String year = excelVersion.getUploadedYear();
        String currentYear = String.valueOf(LocalDateTime.now().getYear());

        if (incrementVersion) {
            if (!Objects.equals(year, currentYear)) {
                return basePrefix + currentYear + VERSION_1_PREFIX;
            } else {
                String versionSubstring = previousExcelVersion.substring(
                        previousExcelVersion.lastIndexOf('V') + 1);
                int version = Integer.parseInt(versionSubstring);
                log.info("Version: {}", version);
                return basePrefix + currentYear + VERSION_PREFIX + (version + 1);
            }
        } else {
            return basePrefix + year + UNDERSCORE + previousExcelVersion;
        }
    }
}