package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FutureSkillCategoryResponseDTO {
    private String categoryName;
    private List<String> questions;
    private String answer;
}