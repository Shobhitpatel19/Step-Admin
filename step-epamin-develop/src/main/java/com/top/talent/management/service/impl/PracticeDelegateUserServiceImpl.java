package com.top.talent.management.service.impl;

import com.top.talent.management.dto.PracticeDelegationDTO;
import com.top.talent.management.dto.PracticeDelegationFeatureDTO;
import com.top.talent.management.service.DelegationService;
import com.top.talent.management.service.PracticeDelegateUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PracticeDelegateUserServiceImpl implements PracticeDelegateUserService {
    private final DelegationService delegationService;

    @Override
    public List<PracticeDelegationFeatureDTO> getDelegatedFeatures() {
        return getPracticeDelegate().getPracticeDelegationFeatures();
    }

    @Override
    public Boolean hasAccessToFeature(String featureName) {
        return getDelegatedFeatures().stream()
                .anyMatch(practiceDelegationFeatureDTO ->
                        practiceDelegationFeatureDTO.getName().equals(featureName));
    }

    @Override
    public Boolean isApprovalRequired() {
        return getPracticeDelegate().getApprovalRequired();
    }

    private PracticeDelegationDTO getPracticeDelegate(){
        return delegationService.getPracticeDelegateByDelegatedTo();
    }
}
