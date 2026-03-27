package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubCategoryRatingDTO {
    private String subCategoryName;
    private Double employeeRating;
}
