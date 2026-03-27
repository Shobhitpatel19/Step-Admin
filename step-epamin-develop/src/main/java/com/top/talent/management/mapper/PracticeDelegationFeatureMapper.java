package com.top.talent.management.mapper;

import com.top.talent.management.dto.PracticeDelegationFeatureDTO;
import com.top.talent.management.entity.PracticeDelegationFeature;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PracticeDelegationFeatureMapper {
    PracticeDelegationFeatureDTO toFeatureDTO(PracticeDelegationFeature practiceDelegationFeature);
}
