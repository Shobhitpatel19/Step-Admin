package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AspirationResponseDTO {
    private Boolean isFormActive;
    private Boolean futureSkillAcknowledgment;
    private Boolean submitAcknowledgment;
    private List<String> aspirationExplanation;
    private List<AspirationPriorityDTO> aspirations;
    private List<AspirationPriorityDTO> previousYearAspirations;
}
