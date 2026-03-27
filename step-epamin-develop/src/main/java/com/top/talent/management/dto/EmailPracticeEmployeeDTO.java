package com.top.talent.management.dto;

import com.top.talent.management.constants.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailPracticeEmployeeDTO {
    private Long uid;
    private String name;
    private SubmissionStatus status;
}
