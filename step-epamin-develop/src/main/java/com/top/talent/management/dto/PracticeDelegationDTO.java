package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PracticeDelegationDTO {

    private String delegatedBy;

    private UserProfile delegatedTo;

    private List<PracticeDelegationFeatureDTO> practiceDelegationFeatures;

    private Boolean approvalRequired;

}
