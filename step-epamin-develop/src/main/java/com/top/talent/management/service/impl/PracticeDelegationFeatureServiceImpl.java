package com.top.talent.management.service.impl;

import com.top.talent.management.entity.PracticeDelegationFeature;
import com.top.talent.management.repository.PracticeDelegationFeatureRepository;
import com.top.talent.management.service.PracticeDelegationFeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PracticeDelegationFeatureServiceImpl implements PracticeDelegationFeatureService {
    private final PracticeDelegationFeatureRepository practiceDelegationFeatureRepository;

    @Override
    public List<PracticeDelegationFeature> getAllFeatures(){
        return practiceDelegationFeatureRepository.findAll();
    }

    @Override
    public List<String> getAllFeaturesName(){
        return getAllFeatures().stream()
                .map(PracticeDelegationFeature::getName)
                .toList();
    }

}
