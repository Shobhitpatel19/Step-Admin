package com.top.talent.management.repository;

import com.top.talent.management.entity.PracticeEmailScheduleLog;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaAuditing
public interface PracticeEmailScheduleLogRepository extends JpaRepository<PracticeEmailScheduleLog, Long> {

    Boolean existsByPracticeEmailIdAndTopTalentExcelVersion(String email, TopTalentExcelVersion excelVersion);
    PracticeEmailScheduleLog findByPracticeEmailId(String email);

}
