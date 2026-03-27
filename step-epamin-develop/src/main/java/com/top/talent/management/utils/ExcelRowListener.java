package com.top.talent.management.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.top.talent.management.constants.Constants;
import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.exception.EmptyFileException;
import com.top.talent.management.exception.InvalidCandidateException;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.TopTalentExcelVersionService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Slf4j
public class ExcelRowListener extends AnalysisEventListener<TopTalentEmployee> {

    private final List<TopTalentEmployee> topTalentEmployeeList = new ArrayList<>();
    private final Set<Long> uidSet = new HashSet<>();
    private final Set<String> errorMessages = new HashSet<>();
    private CustomUserPrincipal customUserPrincipal;
    private final TopTalentExcelVersionService topTalentExcelVersionService;
    private final String fileName;
    private TopTalentExcelVersion topTalentExcelVersion;
    private boolean headerValidated = false;

    public ExcelRowListener(TopTalentExcelVersionService topTalentExcelVersionService, String fileName, CustomUserPrincipal customUserPrincipal) {
        this.topTalentExcelVersionService = topTalentExcelVersionService;
        this.fileName = fileName;
        this.customUserPrincipal=customUserPrincipal;
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if (!headerValidated) {
            Set<String> actualColumns = new HashSet<>(headMap.values());

            List<String> missingColumns = Constants.MANDATORY_COLUMNS.stream()
                    .filter(column -> !actualColumns.contains(column))
                    .toList();

            if (!missingColumns.isEmpty()) {
                throw new InvalidCandidateException(ErrorMessages.INVALID_HEADER);
            }
            headerValidated = true;
        }
    }

    @Override
    public void invoke(TopTalentEmployee topTalentEmployeeRow, AnalysisContext context) {
        if (topTalentExcelVersion == null)
            topTalentExcelVersion = topTalentExcelVersionService.saveVersion(fileName,customUserPrincipal, TopTalentEmployeeUtils.extractVersionName(fileName), TopTalentEmployeeUtils.extractYear(fileName));

        log.info(topTalentExcelVersion.getFileName()+" "+topTalentEmployeeRow.toString());

        validateMandatoryFields(topTalentEmployeeRow);

        if (uidSet.contains(topTalentEmployeeRow.getUid())) {
            throw new InvalidCandidateException(ErrorMessages.DUPLICATE_UID + topTalentEmployeeRow.getUid());
        }
        topTalentEmployeeRow.setUid(topTalentEmployeeRow.getUid());
        topTalentEmployeeRow.setTopTalentExcelVersion(topTalentExcelVersion);
        topTalentEmployeeRow.setCreated(LocalDateTime.now());
        topTalentEmployeeRow.setCreatedBy(customUserPrincipal.getFullName());
        topTalentEmployeeRow.setLastUpdated(LocalDateTime.now());
        topTalentEmployeeRow.setLastUpdatedBy(customUserPrincipal.getFullName());
        uidSet.add(topTalentEmployeeRow.getUid());
        topTalentEmployeeList.add(topTalentEmployeeRow);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (topTalentEmployeeList.isEmpty()) {
            throw new EmptyFileException(ErrorMessages.EMPTY_FILE);
        }
        if (!errorMessages.isEmpty()) {
            throw new InvalidCandidateException(errorMessages);
        }
    }

    public void validateMandatoryFields(TopTalentEmployee employee) {


        if (employee.getUid() == null) {
            throw new InvalidCandidateException(ErrorMessages.NULL_UID);
        }
        if (!String.valueOf(employee.getUid()).matches("^\\d{6}$")) {
            throw new InvalidCandidateException(ErrorMessages.INVALID_UID);
        }

        Map<String, Object> fieldMap = getStringObjectMap(employee);

        fieldMap.forEach((error, value) -> {
            if (value == null) {
                errorMessages.add(error + " for UID: " + employee.getUid());
            }
        });

        Double deliveryFeedbackScore = employee.getDeliveryFeedbackTtScore();
        if (deliveryFeedbackScore != null && (deliveryFeedbackScore < 0 || deliveryFeedbackScore > 4))
        {
            errorMessages.add(ErrorMessages.INVALID_DELIVERY_FEEDBACK_SCORE + " for UID: " + employee.getUid());
        }
    }

    private static Map<String, Object> getStringObjectMap(TopTalentEmployee employee) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put(ErrorMessages.MISSING_DOJ, employee.getDoj());
        fieldMap.put(ErrorMessages.MISSING_TITLE, employee.getTitle());
        fieldMap.put(ErrorMessages.MISSING_RESOURCE_MANAGER, employee.getResourceManager());
        fieldMap.put(ErrorMessages.MISSING_JF_LEVEL, employee.getJfLevel());
        fieldMap.put(ErrorMessages.MISSING_COMPETENCY_PRACTICE, employee.getCompetencyPractice());
        fieldMap.put(ErrorMessages.MISSING_PRIMARY_SKILL, employee.getPrimarySkill());
        fieldMap.put(ErrorMessages.MISSING_TALENT_PROFILE, employee.getTalentProfile());
        fieldMap.put(ErrorMessages.NULL_DELIVERY_FEEDBACK_SCORE, employee.getDeliveryFeedbackTtScore());
        fieldMap.put(ErrorMessages.MISSING_NAME, employee.getName());
        fieldMap.put(ErrorMessages.MISSING_EMAIL, employee.getEmail());
        return fieldMap;
    }
}