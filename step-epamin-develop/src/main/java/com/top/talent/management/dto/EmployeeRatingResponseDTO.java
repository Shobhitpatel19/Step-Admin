package com.top.talent.management.dto;

import com.top.talent.management.constants.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class EmployeeRatingResponseDTO {

    private String name;

    private String jobTitle;

    private String primarySkill;

    private SubmissionStatus status;

    private Double mean;

    private List<CategoryDTO> categories;

    private String message;

}
