package com.top.talent.management.dto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class EmployeeDTO extends UserProfile{
    private String isActive;
    private String talentProfilePreviousYear;
    private String talentProfileCurrentYear;
    private String lastAssessmentDate;
    private String lastAssessmentResult;
    private String benchStatus;
    private String isActiveStepUser;
    private String lastPromotionDate;
}
