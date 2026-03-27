package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubCategoryDTO {
    private String subCategoryName;
    private String description;
    private Double employeeRating;
}
