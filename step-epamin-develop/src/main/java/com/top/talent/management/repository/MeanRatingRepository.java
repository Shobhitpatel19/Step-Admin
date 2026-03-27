package com.top.talent.management.repository;

import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.entity.MeanRating;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaAuditing
public interface MeanRatingRepository extends JpaRepository<MeanRating, Long> {
    Optional<MeanRating> findByUidAndTopTalentExcelVersion(Long uid, TopTalentExcelVersion topTalentExcelVersion);
    Optional<List<MeanRating>> findAllByTopTalentExcelVersion(TopTalentExcelVersion topTalentExcelVersion);

    List<MeanRating> findAllByTopTalentExcelVersionAndSubmissionStatus(TopTalentExcelVersion topTalentExcelVersion, SubmissionStatus submissionStatus);
}
