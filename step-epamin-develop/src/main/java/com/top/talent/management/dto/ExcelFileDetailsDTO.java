package com.top.talent.management.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class ExcelFileDetailsDTO {

    private String excelName;

    private List<String> headers;

    private List<String> mandatoryColumns;

}
