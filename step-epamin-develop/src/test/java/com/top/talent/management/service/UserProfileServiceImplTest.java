package com.top.talent.management.service;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.dto.ApiProfileResponse;
import com.top.talent.management.dto.AssessmentDetailsResponse;
import com.top.talent.management.dto.UserProfile;
import com.top.talent.management.exception.ApiException;
import com.top.talent.management.feign.ApiClient;
import com.top.talent.management.mapper.ApiMapper;
import com.top.talent.management.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileServiceImplTest {

    @Mock
    private ApiClient apiClient;

    @Mock
    private ApiMapper apiMapper;

    @Mock
    private AccessTokenService accessTokenService;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchUserDetailsByUids_Success() {
        // Arrange
        Set<Long> uids = Set.of(1L, 2L, 3L);
        String token = "bearerToken";
        when(accessTokenService.buildBearerToken()).thenReturn(token);

        ApiProfileResponse response = mock(ApiProfileResponse.class);
        List<ApiProfileResponse.Result> results = Arrays.asList(
                buildApiResult("1"),
                buildApiResult("2"),
                buildApiResult("3")
        );
        when(response.getResults()).thenReturn(results);

        UserProfile userProfile1 = UserProfile.builder().build();
        UserProfile userProfile2 = UserProfile.builder().build();
        UserProfile userProfile3 = UserProfile.builder().build();

        when(apiClient.fetchUserDetails(anyString(), eq(token))).thenReturn(response);
        when(apiMapper.mapToUserProfile(results.get(0))).thenReturn(userProfile1);
        when(apiMapper.mapToUserProfile(results.get(1))).thenReturn(userProfile2);
        when(apiMapper.mapToUserProfile(results.get(2))).thenReturn(userProfile3);

        // Act
        userProfileService.fetchUserDetails(uids);

        // Assert
        verify(apiClient, times(1)).fetchUserDetails(anyString(), eq(token));
        verify(apiMapper, times(3)).mapToUserProfile(any());
    }

    @Test
    void testFetchUserDetailsByUids_MultipleResponses() {
        // Arrange
        Set<Long> uids = Set.of(1L, 2L, 3L);
        String token = "bearerToken";
        when(accessTokenService.buildBearerToken()).thenReturn(token);

        ApiProfileResponse response = mock(ApiProfileResponse.class);
        List<ApiProfileResponse.Result> results = Arrays.asList(
                buildApiResult("1"),
                buildApiResult("1"),
                buildApiResult("2"),
                buildApiResult("3")
        );
        results.get(0).getEntity().setCreatedWhen("1732261614399");
        results.get(1).getEntity().setCreatedWhen("1732261614400");
        when(response.getResults()).thenReturn(results);

        UserProfile userProfile1 = UserProfile.builder().build();
        UserProfile userProfile2 = UserProfile.builder().build();
        UserProfile userProfile3 = UserProfile.builder().build();

        when(apiClient.fetchUserDetails(anyString(), eq(token))).thenReturn(response);
        when(apiMapper.mapToUserProfile(results.get(0))).thenReturn(userProfile1);
        when(apiMapper.mapToUserProfile(results.get(1))).thenReturn(userProfile2);
        when(apiMapper.mapToUserProfile(results.get(2))).thenReturn(userProfile3);

        // Act
        userProfileService.fetchUserDetails(uids);

        // Assert
        verify(apiClient, times(1)).fetchUserDetails(anyString(), eq(token));
        verify(apiMapper, times(3)).mapToUserProfile(any());
    }

    @Test
    void testFetchUserDetailsByUids_NoResults() {
        // Arrange
        Set<Long> uids = Set.of(1L, 2L);
        String token = "bearerToken";
        when(accessTokenService.buildBearerToken()).thenReturn(token);

        ApiProfileResponse response = mock(ApiProfileResponse.class);
        when(response.getResults()).thenReturn(Collections.emptyList());
        when(apiClient.fetchUserDetails(anyString(), eq(token))).thenReturn(response);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> userProfileService.fetchUserDetails(uids));
        assertEquals(ErrorMessages.NO_DATA_FOUND, exception.getMessage());
    }

    @Test
    void testFetchUserDetailsByName_Success() {
        // Arrange
        String name = "John Doe";
        String token = "bearerToken";
        when(accessTokenService.buildBearerToken()).thenReturn(token);

        ApiProfileResponse response = mock(ApiProfileResponse.class);
        List<ApiProfileResponse.Result> results = List.of(buildApiResult("1"));
        when(response.getResults()).thenReturn(results);

        when(apiClient.fetchUserDetails(anyString(), eq(token))).thenReturn(response);
        when(apiMapper.mapToUserProfile(any(ApiProfileResponse.Result.class))).thenReturn(UserProfile.builder().fullName("John Doe").firstName("John").lastName("Doe").build());

        // Act
        Set<UserProfile> profiles = userProfileService.fetchUserDetails(name);

        // Assert
        assertEquals(1, profiles.size());
        verify(apiClient).fetchUserDetails(anyString(), eq(token));
        verify(apiMapper, times(1)).mapToUserProfile(any(ApiProfileResponse.Result.class));
    }

    @Test
    void testFetchUserDetailsByName_WithResourceManager() {
        // Arrange
        String name = "John Doe";
        String token = "bearerToken";
        when(accessTokenService.buildBearerToken()).thenReturn(token);

        ApiProfileResponse response = mock(ApiProfileResponse.class);
        List<ApiProfileResponse.Result> results = List.of(buildApiResultWithRm());
        when(response.getResults()).thenReturn(results);

        when(apiClient.fetchUserDetails(anyString(), eq(token))).thenReturn(response);
        when(apiClient.fetchUserDetails("RM1", token)).thenReturn(response);
        when(apiMapper.mapToUserProfile(any())).thenReturn(UserProfile.builder().fullName("John Doe").firstName("John").lastName("Doe").build());

        // Act
        Set<UserProfile> profiles = userProfileService.fetchUserDetails(name);

        // Assert
        assertEquals(1, profiles.size());
        verify(apiClient, times(1)).fetchUserDetails(anyString(), eq(token));
        verify(apiMapper, times(1)).mapToUserProfile(any());
    }

    @Test
    void testFetchUserDetailsByName_NoResults() {
        // Arrange
        String name = "Nonexistent";
        String token = "bearerToken";
        when(accessTokenService.buildBearerToken()).thenReturn(token);

        ApiProfileResponse response = mock(ApiProfileResponse.class);
        when(response.getResults()).thenReturn(Collections.emptyList());
        when(apiClient.fetchUserDetails(anyString(), eq(token))).thenReturn(response);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> userProfileService.fetchUserDetails(name));
        assertEquals(ErrorMessages.NO_DATA_FOUND, exception.getMessage());
    }

    @Test
    void testFetchUserAssessmentRequests_Success() {
        // Arrange
        String employmentId = "EMP123";
        String token = "bearerToken";
        when(accessTokenService.buildBearerToken()).thenReturn(token);

        AssessmentDetailsResponse mockResponse = new AssessmentDetailsResponse();
        when(apiClient.fetchUserAssessmentRequests(anyString(), eq(token))).thenReturn(mockResponse);

        // Act
        Object result = userProfileService.fetchUserAssessmentRequests(employmentId);

        // Assert
        assertEquals(mockResponse, result);
        verify(apiClient).fetchUserAssessmentRequests(anyString(), eq(token));
    }

    @Test
    void testFetchUserAssessmentRequests_NoResults() {
        // Arrange
        String employmentId = "EMP123";
        String token = "bearerToken";
        when(accessTokenService.buildBearerToken()).thenReturn(token);

        when(apiClient.fetchUserAssessmentRequests(anyString(), eq(token))).thenReturn(null);

        // Act
        Object result = userProfileService.fetchUserAssessmentRequests(employmentId);

        // Assert
        assertNull(result);
    }

    @Test
    void testFetchUserByEmail_Success() {
        // Arrange
        String name = "John Doe";
        String token = "bearerToken";
        when(accessTokenService.buildBearerToken()).thenReturn(token);

        ApiProfileResponse response = mock(ApiProfileResponse.class);
        List<ApiProfileResponse.Result> results = List.of(buildApiResult("1"));
        when(response.getResults()).thenReturn(results);

        when(apiClient.fetchUserDetails(anyString(), eq(token))).thenReturn(response);
        when(apiMapper.mapToUserProfile(any())).thenReturn(UserProfile.builder().uid(1L).build());

        // Act
        UserProfile profile = userProfileService.fetchUserByEmail(name);

        // Assert
        assertEquals(1L, profile.getUid());
        verify(apiClient).fetchUserDetails(anyString(), eq(token));
        verify(apiMapper, times(1)).mapToUserProfile(any());
    }

    // Helper methods
    private ApiProfileResponse.Result buildApiResult(String uid) {
        ApiProfileResponse.Result result = new ApiProfileResponse.Result();
        result.setUid(uid);

        ApiProfileResponse.Result.Entity entity = new ApiProfileResponse.Result.Entity();
        entity.setCreatedWhen("1732261614399");
        result.setEntity(entity);

        result.setResourceManager(new ApiProfileResponse.Result.ResourceManager());
        return result;
    }

    private ApiProfileResponse.Result buildApiResultWithRm() {
        ApiProfileResponse.Result result = new ApiProfileResponse.Result();
        result.setUid("1");

        ApiProfileResponse.Result.ResourceManager resourceManager = new ApiProfileResponse.Result.ResourceManager();
        resourceManager.setId("RM1");

        ApiProfileResponse.Result.Entity entity = new ApiProfileResponse.Result.Entity();
        entity.setCreatedWhen("1732261614399");

        result.setEntity(entity);
        result.setResourceManager(resourceManager);
        return result;
    }
}
