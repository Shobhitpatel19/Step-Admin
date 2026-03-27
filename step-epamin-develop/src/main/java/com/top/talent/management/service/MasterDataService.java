package com.top.talent.management.service;

import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.MasterDataResponseDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.security.CustomUserPrincipal;

import java.util.List;
import java.util.Map;

public interface MasterDataService {

    MasterDataResponseDTO viewMasterData(String fileName);

    MasterDataResponseDTO saveEmployeesOfExcelVersion(SubmissionStatus submissionStatus, List<Long> uidS, CustomUserPrincipal customUserPrincipal);

    Map<Long, UserProfile> getUserProfile();

}
