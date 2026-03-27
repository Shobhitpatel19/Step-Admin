package com.top.talent.management.mapper;

import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.dto.EmailPracticeEmployeeDTO;
import com.top.talent.management.dto.PracticeEmployeeDTO;
import com.top.talent.management.dto.TalentProfileDTO;
import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.entity.TopTalentEmployee;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TopTalentEmployeeMapper {

    TopTalentEmployeeDTO employeeDataToEmployeeDTO(TopTalentEmployee topTalentEmployee);

    @Mapping(target = "talentProfileCurrentYear", source = "talentProfile")
    @Mapping(target = "year", source = "topTalentExcelVersion.uploadedYear")
    TalentProfileDTO employeeToTalentProfileDTO(TopTalentEmployee employee);

    @Mapping(target="uid", source="topTalentEmployee.uid")
    @Mapping(target = "email", source = "userProfile.email")
    @Mapping(target="primarySkill", source="userProfile.primarySkill")
    @Mapping(target = "talentProfilePreviousYear", source = "topTalentEmployee.talentProfilePreviousYear")
    @Mapping(target = "talentProfile", source = "topTalentEmployee.talentProfile")
    @Mapping(target = "submissionStatus", expression= "java(submissionStatus)")
    @Mapping(target = "practiceRating", expression= "java(rating)")
    @Mapping(target = "practice" ,source = "topTalentEmployee.competencyPractice")
    PracticeEmployeeDTO toPracticeEmployeeDTO(UserProfile userProfile, TopTalentEmployee topTalentEmployee, @Context SubmissionStatus submissionStatus, @Context Double rating);

    @Mapping(target = "name", source = "topTalentEmployee.name")
    @Mapping(target = "uid", source = "topTalentEmployee.uid")
    @Mapping(target = "status", expression= "java(submissionStatus)")
    EmailPracticeEmployeeDTO toEmailPracticeEmployee(TopTalentEmployee topTalentEmployee, @Context SubmissionStatus submissionStatus);

}