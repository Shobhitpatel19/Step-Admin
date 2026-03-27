package com.top.talent.management.utils;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.exception.InvalidFileFormatException;
import static com.top.talent.management.constants.Constants.FILE_NAME;

public class TopTalentEmployeeUtils {

    private TopTalentEmployeeUtils() {
    }

    public static Boolean validateYear(String fileYear, int currentYear) {
        int extractedYear = Integer.parseInt(fileYear);
        return extractedYear == currentYear;
    }

    public static String extractYear(String fileName) {
        if (fileName.matches(FILE_NAME)) {
            return fileName.split("_")[1].split("\\.")[0];
        } else {
            throw new InvalidFileFormatException(ErrorMessages.INVALID_FILE_FORMAT);
        }
    }

    public static String extractVersionName(String fileName) {
        if (fileName.matches(FILE_NAME)) {
            return fileName.split("_")[2].split("\\.")[0];
        }  else {
            throw new InvalidFileFormatException(ErrorMessages.INVALID_FILE_FORMAT);
        }
    }

    public static String getPreviousVersion(String currentVersion) {
        int versionNumber = Integer.parseInt(currentVersion.substring(1));
        versionNumber -= 1;
        return "V" + versionNumber;
    }

    public static String getPreviousYear(String currentYear) {
        int yearInt = Integer.parseInt(currentYear);
        return String.valueOf(yearInt - 1);
    }

}
