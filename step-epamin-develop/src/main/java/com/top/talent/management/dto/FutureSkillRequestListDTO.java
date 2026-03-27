package com.top.talent.management.dto;

import com.top.talent.management.constants.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FutureSkillRequestListDTO {
    private List<FutureSkillRequestDTO> futureSkills;
    private SubmissionStatus submissionStatus;
}
