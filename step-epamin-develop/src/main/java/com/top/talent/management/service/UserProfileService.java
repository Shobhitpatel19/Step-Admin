package com.top.talent.management.service;

import com.top.talent.management.dto.ApiProfileResponse;
import com.top.talent.management.dto.UserProfile;

import java.util.List;
import java.util.Set;

public interface
UserProfileService {

    Set<UserProfile> fetchUserDetails(Set<Long> uids);
    Set<UserProfile> fetchUserDetails(String name);
    Set<UserProfile> fetchUserDetailsAboveB3(String name);
    Object fetchUserAssessmentRequests(String employmentId);
    UserProfile fetchUserByEmail(String email);
    List<ApiProfileResponse.Result> fetchUser(String query);
    Set<UserProfile> getPracticeHeads();


}
