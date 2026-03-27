package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FutureSkillRequestDTO {
    private String categoryName;
    private String answer;
}
