package com.top.talent.management.service;

import com.top.talent.management.dto.PracticeDelegationDTO;
import com.top.talent.management.entity.Delegation;
import org.springframework.stereotype.Service;

@Service
public interface DelegationService {
    PracticeDelegationDTO createPracticeDelegate(Delegation delegation, String competency);
    PracticeDelegationDTO getPracticeDelegate(String competency);
    PracticeDelegationDTO deletePracticeDelegate(String competency);
    PracticeDelegationDTO getPracticeDelegateByDelegatedTo();
}
