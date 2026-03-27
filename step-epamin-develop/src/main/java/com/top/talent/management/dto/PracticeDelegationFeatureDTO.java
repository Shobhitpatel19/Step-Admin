package com.top.talent.management.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class PracticeDelegationFeatureDTO {
    private String name;
    private String frontendPath;
}
