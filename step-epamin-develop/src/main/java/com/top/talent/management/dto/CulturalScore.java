package com.top.talent.management.dto;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CulturalScore {
    @ExcelProperty("UID")
    private Long uid;
    @ExcelProperty("Culture Score (from feedback)")
    private Double cultureScoreFromFeedback;
}
