package com.top.talent.management.service;

import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.security.CustomUserPrincipal;

public interface TopTalentExcelVersionService {

    TopTalentExcelVersion saveVersion(String fileName, CustomUserPrincipal customUserPrincipal, String versionName, String year);

    void deleteVersion(TopTalentExcelVersion topTalentExcelVersion);

    boolean checkIfFileExists(String fileName);

    TopTalentExcelVersion findLatestVersion();

    TopTalentExcelVersion getExcelVersionForYear(String year, String prefix);

    TopTalentExcelVersion getPreviousYearVersion();

}