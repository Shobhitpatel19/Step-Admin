package com.top.talent.management.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.dto.CulturalScore;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.CultureScoreException;
import com.top.talent.management.exception.EmptyFileException;
import com.top.talent.management.exception.InvalidCandidateException;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.TopTalentExcelVersionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.top.talent.management.utils.CultureScoreUtils.extractVersionName;
import static com.top.talent.management.utils.CultureScoreUtils.extractYear;

@Slf4j
@Getter
@RequiredArgsConstructor
public class
CulturalScoreListener extends AnalysisEventListener<CulturalScore> {

    private final List<CulturalScore> dataList = new ArrayList<>();
    private final String fileName;
    private final Set<Long> uidSet = new HashSet<>();
    private final Set<String> invalidRows = new HashSet<>();

    private final CustomUserPrincipal customUserPrincipal;
    private final TopTalentExcelVersionService topTalentExcelVersionService;
    private TopTalentExcelVersion topTalentExcelVersion;

    @Override
    public void invoke(CulturalScore culturalScoreEmployeeRow, AnalysisContext context) {
        if (topTalentExcelVersion == null)
            topTalentExcelVersion = topTalentExcelVersionService.saveVersion(fileName, customUserPrincipal, extractVersionName(fileName), extractYear(fileName));
        log.info("Parsing Row: {}", culturalScoreEmployeeRow);
        validateRow(culturalScoreEmployeeRow);

        uidSet.add(culturalScoreEmployeeRow.getUid());
        dataList.add(culturalScoreEmployeeRow);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (dataList.isEmpty()) {
            throw new EmptyFileException("Uploaded Excel file is empty.");
        }
        if(!invalidRows.isEmpty()){
            throw new CultureScoreException(invalidRows);
        }
        log.info("All rows processed!");
    }

    private void validateRow(CulturalScore row) {
        if (uidSet.contains(row.getUid())) {
            throw new CultureScoreException(ErrorMessages.DUPLICATE_UID + row.getUid());
        }
        if (row.getUid() == null) {
            throw new CultureScoreException(ErrorMessages.NULL_UID);
        }
        if (!String.valueOf(row.getUid()).matches("^\\d{6}$")) {
            throw new InvalidCandidateException(ErrorMessages.INVALID_UID);
        }
        if (row.getCultureScoreFromFeedback() == null) {
            throw new CultureScoreException(ErrorMessages.NULL_CULTURAL_SCORE + row.getUid());
        }

        if (row.getCultureScoreFromFeedback() < 0 || row.getCultureScoreFromFeedback() > 4) {
            invalidRows.add(ErrorMessages.INVALID_CULTURAL_SCORE + row.getUid());
        }
    }
}
