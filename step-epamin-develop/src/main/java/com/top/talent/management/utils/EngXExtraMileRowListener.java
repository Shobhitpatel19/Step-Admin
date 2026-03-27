package com.top.talent.management.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.dto.EngXExtraMileRating;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.EmptyFileException;
import com.top.talent.management.exception.EngXExtraMileRatingException;
import com.top.talent.management.exception.InvalidCandidateException;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.TopTalentExcelVersionService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.top.talent.management.utils.EngxExtraMileUtils.extractVersionName;
import static com.top.talent.management.utils.EngxExtraMileUtils.extractYear;

@Getter
@Slf4j
public class EngXExtraMileRowListener extends AnalysisEventListener<EngXExtraMileRating> {

    private final List<EngXExtraMileRating> ratingList = new ArrayList<>();
    private final Set<Long> uidSet = new HashSet<>();
    private final String fileName;
    private final Set<String> invalidRows = new HashSet<>();
    private final CustomUserPrincipal customUserPrincipal;
    private final TopTalentExcelVersionService topTalentExcelVersionService;

    private TopTalentExcelVersion topTalentExcelVersion;

    public EngXExtraMileRowListener(String fileName, CustomUserPrincipal customUserPrincipal,
                                    TopTalentExcelVersionService topTalentExcelVersionService) {
        this.fileName = fileName;
        this.customUserPrincipal = customUserPrincipal;
        this.topTalentExcelVersionService = topTalentExcelVersionService;
    }

    @Override
    public void invoke(EngXExtraMileRating row, AnalysisContext context) {
        if (topTalentExcelVersion == null)
            topTalentExcelVersion = topTalentExcelVersionService.saveVersion(fileName, customUserPrincipal,
                    extractVersionName(fileName), extractYear(fileName));

        log.info("{} {}", topTalentExcelVersion.getFileName(), row.toString());

        validateRow(row);

        uidSet.add(row.getUid());
        ratingList.add(row);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (ratingList.isEmpty()) {
            throw new EmptyFileException(ErrorMessages.EMPTY_FILE);
        }
        if(!invalidRows.isEmpty()){
            throw new EngXExtraMileRatingException(invalidRows);
        }
        log.debug("All rows processed!");
    }

    private void validateRow(EngXExtraMileRating row) {
        if (uidSet.contains(row.getUid())) {
            throw new EngXExtraMileRatingException(ErrorMessages.DUPLICATE_UID + row.getUid());
        }
        if (row.getUid() == null) {
            throw new EngXExtraMileRatingException(ErrorMessages.NULL_UID);
        }
        if (!String.valueOf(row.getUid()).matches("^\\d{6}$")) {
            throw new InvalidCandidateException(ErrorMessages.INVALID_UID);
        }
        if (row.getEngXRating() == null || row.getExtraMileRating() == null) {
            throw new EngXExtraMileRatingException(ErrorMessages.NULL_RATING_VALUE+row.getUid());
        }
        if (row.getExtraMileRating() > 4 || row.getExtraMileRating() < 0) {
            invalidRows.add(ErrorMessages.EXTRAMILE_INVALID_RATING_VALUE + row.getUid());
        }
        if (row.getEngXRating() > 4 || row.getEngXRating() < 0) {
            invalidRows.add(ErrorMessages.ENGX_INVALID_RATING_VALUE + row.getUid());
        }

    }

}