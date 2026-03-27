package com.top.talent.management.service.impl;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.entity.Auditable;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.VersionException;
import com.top.talent.management.repository.TopTalentExcelVersionRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.utils.TopTalentEmployeeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static com.top.talent.management.constants.Constants.STEP;
import static com.top.talent.management.constants.Constants.UNDERSCORE;
import static com.top.talent.management.constants.Constants.VERSION_1;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopTalentExcelVersionServiceImpl implements TopTalentExcelVersionService {

    private final TopTalentExcelVersionRepository topTalentExcelVersionRepository;

    public TopTalentExcelVersion saveVersion(String fileName, CustomUserPrincipal customUserPrincipal, String versionName, String year) {

        log.debug("Saving TopTalentExcelVersion with created: {}, createdBy: {}, lastUpdated: {}, lastUpdatedBy: {}",
                LocalDateTime.now(), customUserPrincipal.getFullName(), LocalDateTime.now(), customUserPrincipal.getFullName());

        TopTalentExcelVersion topTalentExcelVersion =
                new TopTalentExcelVersion(
                        fileName,
                        versionName,
                        LocalDateTime.now(), // created time
                        customUserPrincipal.getFullName(), // created by
                        LocalDateTime.now(), // last updated time
                        customUserPrincipal.getFullName(),
                        year
                );
        return topTalentExcelVersionRepository.save(topTalentExcelVersion);
    }

    public void deleteVersion(TopTalentExcelVersion topTalentExcelVersion) {
        topTalentExcelVersionRepository.delete(topTalentExcelVersion);
    }

    public boolean checkIfFileExists(String fileName) {
        return topTalentExcelVersionRepository.existsByFileName(fileName);
    }

    public TopTalentExcelVersion findLatestVersion(){
        String currentYear = String.valueOf(LocalDateTime.now().getYear());

        List<TopTalentExcelVersion> versions = topTalentExcelVersionRepository.findAllByUploadedYear(currentYear);

        if (versions.isEmpty()) {
            throw new VersionException(ErrorMessages.LIST_NOT_FOUND);
        }

        return versions.stream()
                .filter(version -> version.getFileName().startsWith(STEP+UNDERSCORE+currentYear))
                .max(Comparator.comparing(Auditable::getCreated))
                .orElseThrow(() -> new VersionException(ErrorMessages.LIST_NOT_FOUND));
    }


    @Override
    public TopTalentExcelVersion getExcelVersionForYear(String year, String prefix) {
        List<TopTalentExcelVersion> versions = topTalentExcelVersionRepository.findAllByUploadedYear(year);
        if (versions.isEmpty()) {
            throw new VersionException(ErrorMessages.LIST_NOT_FOUND);
        }
        return versions.stream()
                .filter(version -> version.getFileName().startsWith(prefix))
                .max(Comparator.comparing(Auditable::getCreated))
                .orElseThrow(() -> new VersionException(ErrorMessages.LIST_NOT_FOUND));
    }

    @Override
    public TopTalentExcelVersion getPreviousYearVersion() {
        try{
            TopTalentExcelVersion latestVersion = findLatestVersion();
            String currentYear = latestVersion.getUploadedYear();
            String currentVersionName = latestVersion.getVersionName();

            if (currentVersionName.equalsIgnoreCase(VERSION_1)) {
                String previousYear = TopTalentEmployeeUtils.getPreviousYear(currentYear);
                return getExcelVersionForYear(previousYear,
                        STEP + UNDERSCORE + previousYear);

            } else {
                String previousVersion = TopTalentEmployeeUtils.getPreviousVersion(currentVersionName);
                String fileName = STEP + UNDERSCORE + LocalDateTime.now().getYear() + UNDERSCORE +
                        previousVersion ;

                return getExcelVersionForYear(currentYear, fileName);
            }
        }
        catch (VersionException e){
            log.info("No previous year version found");
            return TopTalentExcelVersion.builder()
                    .fileName("NA")
                    .uploadedYear("NA")
                    .versionName("NA")
                    .build();
        }
    }

}
