package com.top.talent.management.service;

import com.top.talent.management.dto.PracticeDelegationDTO;
import com.top.talent.management.dto.PracticeDelegationFeatureDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.service.impl.PracticeDelegateUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PracticeDelegateUserServiceImplTest {
    @Mock
    private DelegationService delegationService;

    @InjectMocks
    private PracticeDelegateUserServiceImpl practiceDelegateUserService;

    @BeforeEach
    public void setup() {
        PracticeDelegationDTO delegateUser = PracticeDelegationDTO.builder()
                .delegatedBy("system")
                .delegatedTo(UserProfile.builder()
                        .firstName("Delegate")
                        .lastName("User").build()
                )
                .approvalRequired(true)
                .practiceDelegationFeatures(
                        List.of(
                                PracticeDelegationFeatureDTO.builder().name("Feature1").build(),
                                PracticeDelegationFeatureDTO.builder().name("Feature2").build()
                        )
                )
                .build();
        when(delegationService.getPracticeDelegateByDelegatedTo())
                .thenReturn(delegateUser);
    }

    @Test
    void testGetDelegatedFeatures() {
        List<PracticeDelegationFeatureDTO> practiceDelegationFeatureDTOS = practiceDelegateUserService.getDelegatedFeatures();

        assertEquals(2, practiceDelegationFeatureDTOS.size());
        assertEquals(List.of(
                PracticeDelegationFeatureDTO.builder().name("Feature1").build(),
                PracticeDelegationFeatureDTO.builder().name("Feature2").build()
        ), practiceDelegationFeatureDTOS);
        assertNotEquals(0, practiceDelegationFeatureDTOS.size());
    }

    @Test
    void testHasAccessToFeature() {
        assertTrue(practiceDelegateUserService.hasAccessToFeature("Feature1"));
        assertTrue(practiceDelegateUserService.hasAccessToFeature("Feature2"));
        assertFalse(practiceDelegateUserService.hasAccessToFeature("Feature3"));
    }

    @Test
    void testIsApprovalRequired() {
        assertTrue(practiceDelegateUserService.isApprovalRequired());
    }

}
