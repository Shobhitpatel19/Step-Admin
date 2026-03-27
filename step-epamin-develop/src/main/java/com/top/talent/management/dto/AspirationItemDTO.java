package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AspirationItemDTO {
    private String title;
    private String description;
    private String inputValue;
    private String assignedRole;
    private String proficiency;
    private String approvedBy;
}
