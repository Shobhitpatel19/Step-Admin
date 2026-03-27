package com.top.talent.management.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CategoryDTO {
    private String categoryName;
    private List<SubCategoryDTO> subCategory;
}