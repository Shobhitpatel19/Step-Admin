package com.top.talent.management.service.impl;

import com.top.talent.management.constants.ErrorMessages;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.entity.EmployeeRating;
import com.top.talent.management.entity.MeanRating;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.entity.User;
import com.top.talent.management.exception.UserNotFoundException;
import com.top.talent.management.repository.MeanRatingRepository;
import com.top.talent.management.repository.PracticeRatingRepository;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.repository.UserRepository;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.MeanRatingService;
import com.top.talent.management.service.ValidateCandidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MeanRatingServiceImpl implements MeanRatingService {

    private final MeanRatingRepository meanRatingRepository;

    private final PracticeRatingRepository practiceRatingRepository;

    private final TopTalentEmployeeRepository topTalentEmployeeRepository;

    private final ValidateCandidateService validateCandidateService;

    private final UserRepository userRepository;



    @Override
    public Double getMeanRating(Long uid, TopTalentExcelVersion topTalentExcelVersion) {

        log.info("Getting mean rating for user with uid {} for the year {} with version {}", uid, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName());

        Optional<MeanRating> meanRatingOptional = meanRatingRepository.findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion);
        return meanRatingOptional.map(MeanRating::getMean).orElse(0.0);
    }

    @Override
    public SubmissionStatus getSubmissionStatus(Long uid, TopTalentExcelVersion topTalentExcelVersion) {
        log.info("Getting submission status for user with uid {} for the year {} with version {}", uid, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName());

        Optional<MeanRating> meanRatingOptional = meanRatingRepository.findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion);
        return meanRatingOptional.map(MeanRating::getSubmissionStatus).orElse(SubmissionStatus.NA);
    }

    @Override
    public void calculateMeanRating(Long uid, String currentUsername, SubmissionStatus status, TopTalentExcelVersion topTalentExcelVersion) {

        log.info("Calculating mean rating for uid {} for the year {} with version {}", uid, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName());
        DecimalFormat format = new DecimalFormat("#.#");
        Double mean = Double.parseDouble(format.format(calculateMean(uid, topTalentExcelVersion)));

        log.info("Saving mean rating for uid {} for the year {} with version {}", uid, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName());
        MeanRating meanRating = setMeanRating(uid, mean, currentUsername, status, topTalentExcelVersion);
        meanRatingRepository.save(meanRating);

        if(status == SubmissionStatus.A) {
            log.info("Saving practice rating for uid {} in table step_top_talent_employees for the year {} with version {}", uid, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName());

            TopTalentEmployee topTalentEmployee = topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion).get();
            topTalentEmployee.setPracticeRating(mean);
            topTalentEmployeeRepository.save(topTalentEmployee);
        }
    }

    private Double calculateMean(Long uid, TopTalentExcelVersion topTalentExcelVersion) {

        List<EmployeeRating> ratings = practiceRatingRepository.findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion);

        Map<Long, List<Double>> categoryMap = ratings.stream()
                .collect(Collectors.groupingBy(
                        rating -> rating.getSubCategory().getCategory().getCategoryId(),
                        HashMap::new,
                        Collectors.mapping(EmployeeRating::getRating, Collectors.toList())
                ));

        List<Double> categoryAverages = categoryMap.values().stream()
                .map(this::calculateAverage)
                .toList();

        return calculateAverage(categoryAverages);
    }

    private Double calculateAverage(List<Double> ratings) {
        return  (ratings.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum())/ratings.size();
    }

    private MeanRating setMeanRating(Long uid, Double mean, String currentUsername, SubmissionStatus status, TopTalentExcelVersion topTalentExcelVersion)
    {
        LocalDateTime now = LocalDateTime.now();

        MeanRating meanRating = MeanRating.builder()
                .uid(uid)
                .mean(mean)
                .submissionStatus(status)
                .topTalentExcelVersion(topTalentExcelVersion)
                .lastUpdatedBy(currentUsername)
                .lastUpdated(now)
                .build();

        if(meanRatingRepository.findByUidAndTopTalentExcelVersion(uid, topTalentExcelVersion).isEmpty())
        {
            meanRating.setCreatedBy(currentUsername);
            meanRating.setCreated(now);
        }
        return meanRating;
    }

    @Override
    public boolean approveAllMeanRatings(TopTalentExcelVersion topTalentExcelVersion, String practice) {
        String loggedInUserEmail = CustomUserPrincipal.getLoggedInUserEmail();

        Long loggedInUserUid = userRepository.findByEmail(loggedInUserEmail)
                .map(User::getUuid)
                .orElseThrow(() -> new UserNotFoundException( ErrorMessages.USER_NOT_FOUND_WITH_EMAIL+ loggedInUserEmail));

        log.info("Approving all mean ratings for practice '{}' for the year '{}' with version '{}' except for the logged-in user (UID: {}).",
                practice, topTalentExcelVersion.getUploadedYear(), topTalentExcelVersion.getVersionName(), loggedInUserUid);

        List<MeanRating> meanRatings = meanRatingRepository.findAllByTopTalentExcelVersionAndSubmissionStatus(topTalentExcelVersion, SubmissionStatus.S).stream()
                .filter(meanRating -> practice.equalsIgnoreCase(topTalentEmployeeRepository.findByUidAndTopTalentExcelVersion(meanRating.getUid(), topTalentExcelVersion).get().getCompetencyPractice())&&
                        !meanRating.getUid().equals(loggedInUserUid))
                .toList();

        if (meanRatings.isEmpty()) {
            return false;
        }

        meanRatings.forEach(rating -> rating.setSubmissionStatus(SubmissionStatus.A));
        meanRatingRepository.saveAll(meanRatings);

        return true;
    }

}
