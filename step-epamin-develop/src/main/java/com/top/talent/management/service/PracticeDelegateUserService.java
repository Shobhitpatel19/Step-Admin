package com.top.talent.management.service;

import com.top.talent.management.dto.PracticeDelegationFeatureDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PracticeDelegateUserService {
    List<PracticeDelegationFeatureDTO> getDelegatedFeatures();
    Boolean hasAccessToFeature(String featureName);
    Boolean isApprovalRequired();
}
