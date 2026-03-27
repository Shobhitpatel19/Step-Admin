package com.top.talent.management.service;

import com.top.talent.management.dto.TalentProfileDTO;
import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.security.CustomUserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface TopTalentEmployeeService {

    List<TopTalentEmployeeDTO> parseAndSaveExcel(MultipartFile file, CustomUserPrincipal customUserPrincipal);

    List<TopTalentEmployee> getAllEmployeeDataByVersionAndYear(String year, String versionName);

    List<TopTalentEmployeeDTO> getAllEmployeeDataByYear();

    List<TopTalentEmployeeDTO> getAllEmployeeDataByUid(Long uid);

    List<TalentProfileDTO> getEmployeeDataForTalentProfile(Long uid);

    List<TopTalentEmployeeDTO> getAllEmployeeDataByLatestVersion();

    List<TopTalentEmployee> saveAll(List<TopTalentEmployee> topTalentEmployees);
}