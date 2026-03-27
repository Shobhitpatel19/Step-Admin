package com.top.talent.management.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FutureSkillPracticeDTO {
    private String practiceName;
    private String practiceHeadName;
    private String submissionStatus;
    private String date;
    private String skills;
    private String submittedBy;
}
