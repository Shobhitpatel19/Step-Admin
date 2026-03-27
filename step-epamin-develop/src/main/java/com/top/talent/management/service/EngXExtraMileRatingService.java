package com.top.talent.management.service;

import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.security.CustomUserPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface EngXExtraMileRatingService {

    List<TopTalentEmployeeDTO> parseAndSaveExcel(MultipartFile file, CustomUserPrincipal customUserPrincipal);

}
