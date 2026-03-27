package com.top.talent.management.utils;

import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.exception.CorruptedFileException;
import com.top.talent.management.exception.EngXExtraMileRatingException;
import com.top.talent.management.exception.InvalidFileFormatException;
import com.top.talent.management.exception.VersionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
public class EngxExtraMileUtils {

    private EngxExtraMileUtils() {}

    public static String extractYear(String fileName) {
        if (fileName == null || !fileName.contains("_")) {
            throw new EngXExtraMileRatingException(ErrorMessages.INVALID_FILE_FORMAT);
        }
        String[] parts = fileName.split("_");
        if (parts.length < 3) {
            throw new EngXExtraMileRatingException(ErrorMessages.INVALID_FILE_FORMAT);
        }
        return parts[2];
    }

    public static String extractVersionName(String fileName) {
        String[] parts = fileName.split("_");
        return parts[3].split("\\.")[0];
    }

    public static Boolean validateYear(String fileYear) {
        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        return fileYear.equals(currentYear);
    }

    public static void validateFileAndFileName(MultipartFile file){
        if (file.isEmpty()) {
            throw new CorruptedFileException(ErrorMessages.EMPTY_FILE);
        }

        if (!Objects.requireNonNull(file.getOriginalFilename()).matches(Constants.HEROES_FILE_NAME)) {
            throw new InvalidFileFormatException(ErrorMessages.INVALID_FILE_FORMAT);
        }
        String year = extractYear(file.getOriginalFilename());

        if (Boolean.FALSE.equals(validateYear(year))) {
            throw new VersionException(ErrorMessages.YEAR_MISMATCH);
        }
    }

}
