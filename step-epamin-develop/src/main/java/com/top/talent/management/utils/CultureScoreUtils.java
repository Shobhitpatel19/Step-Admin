package com.top.talent.management.utils;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.exception.InvalidFileFormatException;

import static com.top.talent.management.constants.Constants.CULTURAL_SCORE_FILE_NAME;

public class CultureScoreUtils {

    private CultureScoreUtils() {
    }

    public static String extractYear(String fileName) {
        if (fileName.matches(CULTURAL_SCORE_FILE_NAME)) {
            return fileName.split("_")[3].split("\\.")[0];
        } else {
            throw new InvalidFileFormatException(ErrorMessages.INVALID_FILE_FORMAT);
        }
    }

    public static String extractVersionName(String fileName) {
        if (fileName.matches(CULTURAL_SCORE_FILE_NAME)) {
            return fileName.split("_")[4].split("\\.")[0];
        } else {
            throw new InvalidFileFormatException(ErrorMessages.INVALID_FILE_FORMAT);
        }
    }
}
