package com.top.talent.management.service;

import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.entity.Category;
import com.top.talent.management.entity.EmployeeRating;
import com.top.talent.management.entity.MeanRating;
import com.top.talent.management.entity.SubCategory;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.repository.MeanRatingRepository;
import com.top.talent.management.repository.PracticeRatingRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.impl.MeanRatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MeanRatingServiceImplTest {

    @Mock
    private MeanRatingRepository meanRatingRepository;
    @Mock
    private PracticeRatingRepository practiceRatingRepository;
    @Mock
    private TopTalentEmployeeRepository topTalentEmployeeRepository;
    @Mock
    private ValidateCandidateService validateCandidateService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MeanRatingServiceImpl meanRatingService;


    private final long uid = 653000L;

    private TopTalentExcelVersion topTalentExcelVersion;

    @BeforeEach
    void setUp() {
        topTalentExcelVersion = mock(TopTalentExcelVersion.class);

    }

    @Test
    void testGetMeanRating_WhenRatingIsPresent() {
        when(meanRatingRepository.findByUidAndTopTalentExcelVersion(anyLong(), any(TopTalentExcelVersion.class)))
                .thenReturn(Optional.of(getMockMeanRating()));

        Double result = meanRatingService.getMeanRating(uid, topTalentExcelVersion);
        assertEquals(Double.valueOf(4.5), result);
    }

    @Test
    void testGetMeanRating_WhenRatingIsAbsent() {
        when(meanRatingRepository.findByUidAndTopTalentExcelVersion(anyLong(), any(TopTalentExcelVersion.class)))
                .thenReturn(Optional.empty());

        Double result = meanRatingService.getMeanRating(uid, topTalentExcelVersion);
        assertEquals(0.0, result);
    }

    @Test
    void testGetSubmissionStatus_WhenRatingIsPresent_Status_D() {
        when(meanRatingRepository.findByUidAndTopTalentExcelVersion(anyLong(), any(TopTalentExcelVersion.class)))
                .thenReturn(Optional.of(getMockMeanRatingWithStatus(SubmissionStatus.D)));

        SubmissionStatus result = meanRatingService.getSubmissionStatus(uid, topTalentExcelVersion);
        assertEquals(SubmissionStatus.D, result);
    }

    @Test
    void testGetSubmissionStatus_WhenRatingIsPresent_Status_S() {
        when(meanRatingRepository.findByUidAndTopTalentExcelVersion(anyLong(), any(TopTalentExcelVersion.class)))
                .thenReturn(Optional.of(getMockMeanRatingWithStatus(SubmissionStatus.S)));

        SubmissionStatus result = meanRatingService.getSubmissionStatus(uid, topTalentExcelVersion);
        assertEquals(SubmissionStatus.S, result);
    }

    @Test
    void testGetSubmissionStatus_WhenRatingIsAbsent() {
        when(meanRatingRepository.findByUidAndTopTalentExcelVersion(anyLong(), any(TopTalentExcelVersion.class)))
                .thenReturn(Optional.empty());

        SubmissionStatus result = meanRatingService.getSubmissionStatus(uid, topTalentExcelVersion);
        assertEquals(SubmissionStatus.NA, result);
    }

    @Test
    void testCalculateMeanRating_WhenStatusIs_A() {
        TopTalentEmployee mockTopTalentEmployee = getMockTopTalentEmployee(topTalentExcelVersion);
        when(practiceRatingRepository.findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion)).thenReturn(getMockEmployeeRatings());
        when(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion)).thenReturn(Optional.of(mockTopTalentEmployee));

        meanRatingService.calculateMeanRating(uid, "System", SubmissionStatus.A, topTalentExcelVersion);

        assertEquals(4.5, mockTopTalentEmployee.getPracticeRating());

        verify(practiceRatingRepository).findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion);
        verify(meanRatingRepository).save(any(MeanRating.class));
        verify(topTalentEmployeeRepository).findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion);
        verify(topTalentEmployeeRepository).save(any(TopTalentEmployee.class));
    }

    @Test
    void testCalculateMeanRating_WhenStatusIs_D() {

        meanRatingService.calculateMeanRating(uid, "System", SubmissionStatus.D, topTalentExcelVersion);

        verify(meanRatingRepository, times(1)).save(any(MeanRating.class));
        verify(topTalentEmployeeRepository, never()).save(any(TopTalentEmployee.class));
    }

    @Test
    void testCalculateMeanRating_WhenStatusIs_S() {

        meanRatingService.calculateMeanRating(uid, "System", SubmissionStatus.S, topTalentExcelVersion);

        verify(meanRatingRepository, times(1)).save(any(MeanRating.class));
        verify(topTalentEmployeeRepository, never()).save(any(TopTalentEmployee.class));
    }

    @Test
    void approveAllMeanRatings_whenNoMeanRatingsFound_shouldReturnFalse() {
        String practice = "Engineering";
        String loggedInUserEmail = "test_user@example.com";

        try (MockedStatic<CustomUserPrincipal> mockedCustomUserPrincipal = Mockito.mockStatic(CustomUserPrincipal.class)) {
            mockedCustomUserPrincipal.when(CustomUserPrincipal::getLoggedInUserEmail).thenReturn(loggedInUserEmail);
            when(userRepository.findByEmail(loggedInUserEmail))
                    .thenReturn(Optional.of(User.builder().uuid(123L).email(loggedInUserEmail).build()));
            when(meanRatingRepository.findAllByTopTalentExcelVersionAndSubmissionStatus(topTalentExcelVersion, SubmissionStatus.S))
                    .thenReturn(List.of());
            boolean result = meanRatingService.approveAllMeanRatings(topTalentExcelVersion, practice);
            assertFalse(result);
            verify(meanRatingRepository, never()).saveAll(any());
        }
    }

    @Test
    void approveAllMeanRatings_whenMeanRatingsFoundAndMatchesPractice_shouldReturnTrue() {
        String practice = "Engineering";
        String loggedInUserEmail = "test_user@example.com";
        Long loggedInUserUid = 3L;

        try (MockedStatic<CustomUserPrincipal> mockedCustomUserPrincipal = Mockito.mockStatic(CustomUserPrincipal.class)) {
            mockedCustomUserPrincipal.when(CustomUserPrincipal::getLoggedInUserEmail).thenReturn(loggedInUserEmail);
            when(userRepository.findByEmail(loggedInUserEmail))
                    .thenReturn(Optional.of(User.builder().uuid(loggedInUserUid).email(loggedInUserEmail).build()));
            when(meanRatingRepository.findAllByTopTalentExcelVersionAndSubmissionStatus(topTalentExcelVersion, SubmissionStatus.S))
                    .thenReturn(
                            List.of(
                                    MeanRating.builder().uid(1L).submissionStatus(SubmissionStatus.S).build(),
                                    MeanRating.builder().uid(2L).submissionStatus(SubmissionStatus.S).build(),
                                    MeanRating.builder().uid(loggedInUserUid).submissionStatus(SubmissionStatus.S).build() // Logged-in user rating excluded
                            )
                    );

            when(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(1L, topTalentExcelVersion))
                    .thenReturn(Optional.of(TopTalentEmployee.builder().competencyPractice(practice).build()));
            when(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(2L, topTalentExcelVersion))
                    .thenReturn(Optional.of(TopTalentEmployee.builder().competencyPractice(practice).build()));
            when(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(loggedInUserUid, topTalentExcelVersion))
                    .thenReturn(Optional.of(TopTalentEmployee.builder().competencyPractice(practice).build()));

            boolean result = meanRatingService.approveAllMeanRatings(topTalentExcelVersion, practice);

            assertTrue(result);

            ArgumentCaptor<List<MeanRating>> captor = ArgumentCaptor.forClass(List.class);
            verify(meanRatingRepository).saveAll(captor.capture());

            List<MeanRating> capturedRatings = captor.getValue();
            assertEquals(2, capturedRatings.size());
            assertEquals(SubmissionStatus.A, capturedRatings.get(0).getSubmissionStatus());
            assertEquals(SubmissionStatus.A, capturedRatings.get(1).getSubmissionStatus());
        }
    }

    @Test
    void approveAllMeanRatings_whenMeanRatingsFoundButNotMatchingPractice_shouldReturnFalse() {
        String practice = "Marketing";
        String loggedInUserEmail = "test_user@example.com";

        try (MockedStatic<CustomUserPrincipal> mockedCustomUserPrincipal = Mockito.mockStatic(CustomUserPrincipal.class)) {
            mockedCustomUserPrincipal.when(CustomUserPrincipal::getLoggedInUserEmail).thenReturn(loggedInUserEmail);

            when(userRepository.findByEmail(loggedInUserEmail))
                    .thenReturn(Optional.of(User.builder().uuid(123L).email(loggedInUserEmail).build()));

            when(meanRatingRepository.findAllByTopTalentExcelVersionAndSubmissionStatus(topTalentExcelVersion, SubmissionStatus.S))
                    .thenReturn(getMockMeanRatingListWithStatus(SubmissionStatus.S));

            when(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(653000L, topTalentExcelVersion))
                    .thenReturn(Optional.of(TopTalentEmployee.builder().competencyPractice("Engineering").build()));

            boolean result = meanRatingService.approveAllMeanRatings(topTalentExcelVersion, practice);

            assertFalse(result);
            verify(meanRatingRepository, never()).saveAll(any());
        }
    }

    private List<MeanRating> getMockMeanRatingListWithStatus(SubmissionStatus status) {
        return List.of(
                MeanRating.builder().uid(653000L).submissionStatus(status).build()
        );
    }
    private MeanRating getMockMeanRating() {
        return MeanRating.builder()
                .uid(uid)
                .mean(4.5)
                .build();
    }

    private MeanRating getMockMeanRatingWithStatus(SubmissionStatus status) {
        return MeanRating.builder()
                .uid(uid)
                .submissionStatus(status)
                .build();
    }

    private List<EmployeeRating> getMockEmployeeRatings()
    {
        Category category = Category.builder()
                .categoryId(1L)
                .categoryName("category1")
                .build();

        SubCategory subCategory1 = SubCategory.builder()
                .subCategoryId(1L)
                .subCategoryName("subcategory1")
                .category(category)
                .build();

        SubCategory subCategory2 = SubCategory.builder()
                .subCategoryId(2L)
                .subCategoryName("subcategory2")
                .category(category)
                .build();

        EmployeeRating rating1 = EmployeeRating.builder()
                .uid(uid)
                .rating(4.0)
                .subCategory(subCategory1)
                .build();

        EmployeeRating rating2 = EmployeeRating.builder()
                .uid(uid)
                .rating(5.0)
                .subCategory(subCategory2)
                .build();

        return List.of(rating1, rating2);
    }

    private TopTalentEmployee getMockTopTalentEmployee(TopTalentExcelVersion latestVersion)
    {
        return TopTalentEmployee.builder()
                .uid(uid)
                .practiceRating(4.5)
                .topTalentExcelVersion(latestVersion)
                .competencyPractice("Engineering")
                .build();
    }

}
