package com.top.talent.management.mapper;

import com.top.talent.management.dto.EmployeeDTO;
import com.top.talent.management.dto.UserDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.entity.TopTalentEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "userProfile.fullName", target = "fullName")
    @Mapping(source = "userProfile.firstName", target = "firstName")
    @Mapping(source = "userProfile.lastName", target = "lastName")
    @Mapping(source = "userProfile.uid", target = "uid")
    @Mapping(source = "userProfile.email", target = "email")
    @Mapping(source = "userProfile.photo", target = "photo")
    @Mapping(source = "userProfile.jobDesignation", target = "jobDesignation")
    @Mapping(source = "userProfile.primarySkill", target = "primarySkill")
    @Mapping(source = "userProfile.jobLevel", target = "jobLevel")
    @Mapping(source = "userProfile.lastPromotionDate", target = "lastPromotionDate")
    @Mapping(source = "userProfile.officeAddress", target = "officeAddress")
    @Mapping(source = "topTalentEmployee.talentProfilePreviousYear", target = "talentProfilePreviousYear")
    @Mapping(source = "topTalentEmployee.talentProfile", target = "talentProfileCurrentYear")
    @Mapping(source = "user.isActive", target = "isActiveStepUser")
    EmployeeDTO toEmployeeProfileDTO(UserProfile userProfile, TopTalentEmployee topTalentEmployee, UserDTO user);
}
