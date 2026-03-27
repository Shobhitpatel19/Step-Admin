package com.top.talent.management.service.impl;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.dto.*;
import com.top.talent.management.exception.ApiException;
import com.top.talent.management.feign.ApiClient;
import com.top.talent.management.mapper.ApiMapper;
import com.top.talent.management.service.AccessTokenService;
import com.top.talent.management.service.UserProfileService;
import com.top.talent.management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.top.talent.management.constants.ApiQueryConstants.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final ApiClient apiClient;
    private final ApiMapper apiMapper;
    private final AccessTokenService accessTokenService;
    private final UserService userService;
    @Override
    public Set<UserProfile> fetchUserDetails(Set<Long> uids) {
        int batchSize = 10;
        List<Long> uidList = new ArrayList<>(uids);

        String bearerToken = accessTokenService.buildBearerToken();
        Set<UserProfile> allProfiles = new HashSet<>();
        List<List<Long>> batches = IntStream.range(0, (uidList.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> uidList.subList(i * batchSize, Math.min((i + 1) * batchSize, uidList.size())))
                .toList();

        batches.parallelStream()
                .map(this::buildQuery)
                .map(query -> processApiUserDetails(query, bearerToken))
                .forEach(allProfiles::addAll);

        return allProfiles;
    }

    private String buildQuery(List<Long> batch) {
        return QUERY_UID + batch.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + ")";
    }

    @Override
    public Set<UserProfile> fetchUserDetails(String name) {
        log.info("Fetching user details for name: {}", name);
        String query = QUERY_PROFILE_EMPLOYEE + LOGICAL_AND
                + QUERY_NAME + "'" + name + "'";
        Set<UserProfile> userProfileSet = processApiUserDetails(query, accessTokenService.buildBearerToken());
        return userProfileSet.parallelStream()
                .filter(userProfile ->
                        userProfile.getFullName().toLowerCase().contains(name.toLowerCase()))
                .sorted(Comparator.comparing(UserProfile::getFullName))
                .limit(15)
                .collect(Collectors.toSet());
    }



    @Override
    public Set<UserProfile> fetchUserDetailsAboveB3(String name) {
        log.info("Fetching user details for name above b3: {}", name);
        String query = QUERY_PROFILE_EMPLOYEE + LOGICAL_AND
                + QUERY_NAME + "'" + name + "'" + LOGICAL_AND + QUERY_ABOVE_B3  ;
        Set<UserProfile> userProfileSet = processApiUserDetails(query, accessTokenService.buildBearerToken());
        return userProfileSet.parallelStream()
                .filter(userProfile ->
                        userProfile.getFullName().toLowerCase().contains(name.toLowerCase()))
                .sorted(Comparator.comparing(UserProfile::getFullName))
                .limit(15)
                .collect(Collectors.toSet());
    }

    @Override
    public UserProfile fetchUserByEmail(String email) {
        log.info("Fetching user details by email: {}", email);

        String query = QUERY_PROFILE_EMPLOYEE + LOGICAL_AND
                + QUERY_EMAIL + "'" + email + "'";
        Set<UserProfile> fetched = processApiUserDetails(query, accessTokenService.buildBearerToken());

        return fetched.isEmpty() ? null : (UserProfile) fetched.toArray()[0];
    }

    @Override
    public List<ApiProfileResponse.Result> fetchUser(String query){
        log.info("Fetching user's complete details by email: {}", query);
        String bearerToken = accessTokenService.buildBearerToken();
        ApiProfileResponse response = apiClient.fetchUserDetails(query, bearerToken);

        return validateAndFilterResponse(query, response);
    }
    public BenchHistoryResponse fetchBenchHistory(String employmentId) {
        log.info("Fetching bench history for Employment ID: {}", employmentId);
        String query = QUERY_BENCH_STATUS + employmentId;
        String bearerToken = accessTokenService.buildBearerToken();
        return apiClient.fetchBenchHistory(query, bearerToken);

    }

    @Override
    public Set<UserProfile> getPracticeHeads() {
        List<UserDTO> practiceHeads = userService.getUsersWithPracticeHeadRole();
        log.info("PracticeHeads {}", practiceHeads);

        Set<Long> practiceHeadUIDs = practiceHeads.stream()
                .map(UserDTO::getUuid)
                .collect(Collectors.toSet());

        return fetchUserDetails(practiceHeadUIDs);
    }

    @Override
    public AssessmentDetailsResponse fetchUserAssessmentRequests(String employmentId) {
        log.info("Fetching assessment requests for Employment ID: {}", employmentId);
        String query = QUERY_CANDIDATE_ID + employmentId;
        String bearerToken = accessTokenService.buildBearerToken();
        return apiClient.fetchUserAssessmentRequests(query, bearerToken);
    }

    private Set<UserProfile> processApiUserDetails(String query, String bearerToken) {
        ApiProfileResponse response = apiClient.fetchUserDetails(query, bearerToken);

        List<ApiProfileResponse.Result> latestResults = validateAndFilterResponse(query, response);

        return latestResults.stream()
                .map(apiMapper::mapToUserProfile)
                .collect(Collectors.toSet());
    }


    private static List<ApiProfileResponse.Result> validateAndFilterResponse(String query, ApiProfileResponse response) {
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            log.error(ErrorMessages.NO_RESPONSE_QUERY, query);
            throw new ApiException(ErrorMessages.NO_DATA_FOUND);
        }
        Map<String, ApiProfileResponse.Result> latestResultsMap = new HashMap<>();
        response.getResults().forEach(result -> {
            String uid = result.getUid();
            if (uid != null) {
                if (latestResultsMap.containsKey(uid)) {
                    String existingDate = latestResultsMap.get(uid).getEntity().getCreatedWhen();
                    String currentDate = result.getEntity().getCreatedWhen();
                    if (currentDate != null && (existingDate == null || currentDate.compareTo(existingDate) > 0)) {
                        latestResultsMap.put(uid, result);
                    }
                } else {
                    latestResultsMap.put(uid, result);
                }
            }
        });
        return latestResultsMap.values().stream().toList();
    }

}