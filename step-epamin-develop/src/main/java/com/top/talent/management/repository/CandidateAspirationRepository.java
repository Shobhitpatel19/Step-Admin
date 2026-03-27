package com.top.talent.management.repository;

import com.top.talent.management.constants.AspirationPriority;
import com.top.talent.management.constants.SubmissionStatus;
import com.top.talent.management.entity.CandidateAspiration;
import com.top.talent.management.entity.CandidateAspirationId;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaAuditing
public interface CandidateAspirationRepository extends JpaRepository<CandidateAspiration, CandidateAspirationId> {

    List<CandidateAspiration> findByUidAndTopTalentExcelVersion(Long uid, TopTalentExcelVersion version);

    List<CandidateAspiration> findByUidAndPriorityAndTopTalentExcelVersion(Long uid, AspirationPriority priority, TopTalentExcelVersion topTalentExcelVersion);

    List<CandidateAspiration> findBySubmissionStatus(SubmissionStatus status);

    Optional<CandidateAspiration> findByUidAndAspirationDetailId(Long uid, Long aspirationDetailId);
}
