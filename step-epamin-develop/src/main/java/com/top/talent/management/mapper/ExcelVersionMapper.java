package com.top.talent.management.mapper;

import com.top.talent.management.dto.TopTalentExcelVersionDTO;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExcelVersionMapper {

    TopTalentExcelVersionDTO convertToDTO(TopTalentExcelVersion topTalentExcelVersion);
}
