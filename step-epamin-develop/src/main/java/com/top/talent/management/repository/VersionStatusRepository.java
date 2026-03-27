package com.top.talent.management.repository;

import com.top.talent.management.entity.VersionStatus;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaAuditing
public interface VersionStatusRepository extends JpaRepository<VersionStatus,Long> {

    Optional<VersionStatus> findByTopTalentExcelVersion(TopTalentExcelVersion topTalentExcelVersion);
}
