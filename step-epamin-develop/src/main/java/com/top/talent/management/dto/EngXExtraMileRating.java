package com.top.talent.management.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EngXExtraMileRating {

    @ExcelProperty("UID")
    private Long uid;

    @ExcelProperty("Email")
    private String email;

    @ExcelProperty("Rating Score for EngX")
    private Long engXRating;

    @ExcelProperty("Rating for Extra Mile")
    private Long extraMileRating;
}
