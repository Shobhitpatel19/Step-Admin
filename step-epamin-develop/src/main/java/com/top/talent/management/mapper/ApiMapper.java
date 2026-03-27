package com.top.talent.management.mapper;

import com.top.talent.management.dto.ApiProfileResponse;
import com.top.talent.management.dto.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static com.top.talent.management.constants.Constants.EMPTY_STRING;
import static com.top.talent.management.constants.UrlConstants.FALLBACK_IMAGE_URL;

@Mapper(componentModel = "spring")
public interface ApiMapper {

    @Mapping(target = "firstName", source = "businessName.firstName", defaultValue = EMPTY_STRING)
    @Mapping(target = "lastName", source = "businessName.lastName", defaultValue = EMPTY_STRING)
    @Mapping(target = "employmentId", source = "employmentId", defaultValue = EMPTY_STRING)
    @Mapping(target = "fullName", source = "nativeFullName", defaultValue = EMPTY_STRING)
    @Mapping(target = "photo", source = "photoUrl", defaultValue = FALLBACK_IMAGE_URL)
    @Mapping(target = "jobDesignation", source = "jobTitle.name", defaultValue = EMPTY_STRING)
    @Mapping(target = "officeAddress", source = "worksiteLocation.name", defaultValue = EMPTY_STRING)
    @Mapping(target = "uid", expression = "java(Long.parseLong(result.getUid()))")
    @Mapping(target = "email", source = "businessEmail", defaultValue = EMPTY_STRING)
    @Mapping(target = "primarySkill", source = "primarySkill.name", defaultValue = EMPTY_STRING)
    @Mapping(target = "jobLevel", source = "jobFunction.name",  defaultValue = EMPTY_STRING)
    @Mapping(target = "jobTrack", source = "jobFunctionTrack", defaultValue = EMPTY_STRING)
    @Mapping(target = "jobTrackLevel", source = "jobFunctionLevel", defaultValue = EMPTY_STRING)
    @Mapping(target = "unit", source = "unit.name", defaultValue = EMPTY_STRING)
    @Mapping(target = "profileType", source = "profileType.name", defaultValue = EMPTY_STRING)
    @Mapping(target = "lastPromotionDate", source = "jobFunctionEffectiveFrom", defaultValue = EMPTY_STRING)
    UserProfile mapToUserProfile(ApiProfileResponse.Result result);


}
