package com.top.talent.management.utils;

import com.top.talent.management.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitialSetupUtilsTest {
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SubCategoryRepository subCategoryRepository;
    @Mock
    private PracticeDelegationFeatureRepository practiceDelegationFeatureRepository;
    @InjectMocks
    private InitialSetupUtils initialSetupUtils;

    @Mock
    private EmailCategoriesRepository emailCategoriesRepository;
    @Mock
    private FutureSkillCategoryRepository futureSkillCategoryRepository;

    @Test
    void testRun() {
        when(roleRepository.findAll()).thenReturn(List.of());
        when(roleRepository.saveAll(anyList())).thenReturn(List.of());

        when(categoryRepository.findAll()).thenReturn(List.of());
        when(categoryRepository.saveAll(anyList())).thenReturn(List.of());

        when(subCategoryRepository.findAll()).thenReturn(List.of());
        when(subCategoryRepository.saveAll(anyList())).thenReturn(List.of());

        when(practiceDelegationFeatureRepository.findAll()).thenReturn(List.of());
        when(practiceDelegationFeatureRepository.saveAll(anyList())).thenReturn(List.of());

        when(futureSkillCategoryRepository.findAll()).thenReturn(List.of());
        when(futureSkillCategoryRepository.saveAll(anyList())).thenReturn(List.of());

        when(emailCategoriesRepository.findAll()).thenReturn(List.of());
        when(emailCategoriesRepository.saveAll(anyList())).thenReturn(List.of());

        initialSetupUtils.run();

        verify(roleRepository).saveAll(anyList());
        verify(categoryRepository).saveAll(anyList());
        verify(subCategoryRepository).saveAll(anyList());
        verify(practiceDelegationFeatureRepository).saveAll(anyList());
        verify(futureSkillCategoryRepository).saveAll(anyList());
        verify(emailCategoriesRepository).saveAll(anyList());
    }

}
