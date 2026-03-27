package com.top.talent.management.service;

import com.top.talent.management.entity.PracticeDelegationFeature;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PracticeDelegationFeatureService {
    List<PracticeDelegationFeature> getAllFeatures();
    List<String> getAllFeaturesName();
}
