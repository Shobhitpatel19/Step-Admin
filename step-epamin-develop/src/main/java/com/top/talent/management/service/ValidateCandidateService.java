package com.top.talent.management.service;

import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.security.CustomUserPrincipal;

public interface ValidateCandidateService {
   TopTalentEmployee isValidCandidate(Long uid, CustomUserPrincipal customUserPrincipal, TopTalentExcelVersion topTalentExcelVersion);
}
