package com.top.talent.management.mapper;

import com.top.talent.management.dto.PracticeDelegationDTO;
import com.top.talent.management.entity.Delegation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PracticeDelegationFeatureMapper.class})
public interface PracticeDelegationMapper {
    @Mapping(target = "delegatedTo", ignore = true)
    PracticeDelegationDTO toPracticeDelegationDto(Delegation delegation);
}
