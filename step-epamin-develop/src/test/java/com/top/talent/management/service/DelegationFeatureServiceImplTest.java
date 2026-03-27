package com.top.talent.management.service;

import com.top.talent.management.entity.PracticeDelegationFeature;
import com.top.talent.management.repository.PracticeDelegationFeatureRepository;
import com.top.talent.management.service.impl.PracticeDelegationFeatureServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DelegationFeatureServiceImplTest {

    @Mock
    private PracticeDelegationFeatureRepository practiceDelegationFeatureRepository;

    @InjectMocks
    private PracticeDelegationFeatureServiceImpl practiceDelegationFeatureService;

    @BeforeEach
    public void setup(){
        when(practiceDelegationFeatureRepository.findAll())
                .thenReturn(
                        List.of(
                                PracticeDelegationFeature.builder().name("Feature1").build(),
                                PracticeDelegationFeature.builder().name("Feature2").build()
                        )
                );
    }

    @Test
    public void testGetAllFeatures(){
        List<PracticeDelegationFeature> practiceDelegationFeatures = practiceDelegationFeatureService.getAllFeatures();
        assertEquals(2, practiceDelegationFeatures.size());
        assertEquals(List.of(
                PracticeDelegationFeature.builder().name("Feature1").build(),
                PracticeDelegationFeature.builder().name("Feature2").build()
        ), practiceDelegationFeatures);
    }

    @Test
    public void testGetAllFeaturesName(){
        List<String> practiceDelegationFeaturesNames = practiceDelegationFeatureService.getAllFeaturesName();
        assertEquals(2, practiceDelegationFeaturesNames.size());
        assertEquals(List.of(
                "Feature1",
                "Feature2"
        ), practiceDelegationFeaturesNames);
    }

}
