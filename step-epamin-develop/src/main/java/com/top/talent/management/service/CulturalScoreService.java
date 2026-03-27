package com.top.talent.management.service;

import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.security.CustomUserPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CulturalScoreService {
    List<TopTalentEmployeeDTO> parseAndSaveCulturalScore(MultipartFile file, CustomUserPrincipal customUserPrincipal);
}