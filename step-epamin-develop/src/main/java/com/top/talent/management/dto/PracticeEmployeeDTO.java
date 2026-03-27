package com.top.talent.management.dto;

import com.top.talent.management.constants.SubmissionStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PracticeEmployeeDTO extends UserProfile{

    private String practice;
    private String talentProfilePreviousYear;
    private String talentProfile;
    private SubmissionStatus submissionStatus;
    private Double practiceRating;
}
