package com.top.talent.management.service;

import com.top.talent.management.dto.FutureSkillPracticeDTO;
import com.top.talent.management.dto.FutureSkillRequestListDTO;
import com.top.talent.management.dto.FutureSkillResponseDTO;
import com.top.talent.management.security.CustomUserPrincipal;

import java.util.List;

public interface FutureSkillService {
    FutureSkillResponseDTO getFutureSkill(CustomUserPrincipal userPrincipal);
    String saveFutureSkill(CustomUserPrincipal userPrincipal, FutureSkillRequestListDTO futureSkillRequestListDTO);
    void notifyIfIdentificationPhaseEnded();
    List<FutureSkillPracticeDTO> getPracticeDetailsAndSubmissionStatus();

}
