package com.top.talent.management.mapper;

import com.top.talent.management.dto.AspirationItemDTO;
import com.top.talent.management.dto.AspirationResponseDTO;
import com.top.talent.management.entity.CandidateAspiration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateAspirationMapper {

    @Mapping(target = "title", source = "aspiration.aspirationDetail.title")
    @Mapping(target = "description", source = "aspiration.aspirationDetail.description")
    @Mapping(target = "inputValue", source = "aspiration.inputValue")
    @Mapping(target = "assignedRole", source = "aspiration.assignedRole")
    @Mapping(target = "proficiency", source = "aspiration.proficiency")
    @Mapping(target = "approvedBy", source = "aspiration.approvedBy")
    AspirationItemDTO mapToAspirationItemDTO(CandidateAspiration aspiration);

    @Mapping(target = "isFormActive", ignore = true)
    @Mapping(target = "aspirationExplanation", ignore = true)
    AspirationResponseDTO mapToAspirationResponseDTO(CandidateAspiration aspiration);
}
