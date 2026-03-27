package com.top.talent.management.dto;

import com.top.talent.management.constants.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class FutureSkillResponseDTO {
    private String practiceName;
    private String practiceHeadName;
    private SubmissionStatus submissionStatus;
    private List<FutureSkillCategoryResponseDTO> categories;
    private LocalDateTime lastUpdated;
}