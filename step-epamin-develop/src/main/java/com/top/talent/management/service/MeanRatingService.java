package com.top.talent.management.service;

import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.entity.TopTalentExcelVersion;

public interface MeanRatingService {
    void calculateMeanRating(Long uid, String currentUsername, SubmissionStatus status, TopTalentExcelVersion topTalentExcelVersion);

    SubmissionStatus getSubmissionStatus(Long uid, TopTalentExcelVersion topTalentExcelVersion);

    Double getMeanRating(Long uid, TopTalentExcelVersion topTalentExcelVersion);
    
    boolean approveAllMeanRatings(TopTalentExcelVersion topTalentExcelVersion, String practice);
}
