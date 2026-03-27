package com.top.talent.management.service;

import com.top.talent.management.dto.ExcelFileDetailsDTO;

public interface ExcelFileDetailsService {

     ExcelFileDetailsDTO generateExcelFile(String excelType);
}
