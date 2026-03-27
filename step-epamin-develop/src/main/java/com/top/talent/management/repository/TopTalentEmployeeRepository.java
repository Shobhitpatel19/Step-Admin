package com.top.talent.management.repository;

import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentEmployeeId;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaAuditing
public interface TopTalentEmployeeRepository extends JpaRepository<TopTalentEmployee, TopTalentEmployeeId> {

    List<TopTalentEmployee> findAllByUid(Long uid);

    Optional<TopTalentEmployee> findByEmail(String email);

    List<TopTalentEmployee> findByCompetencyPractice(String competency);

    List<TopTalentEmployee> findAllByTopTalentExcelVersion(TopTalentExcelVersion topTalentExcelVersion);

    List<TopTalentEmployee> findAllByCompetencyPracticeAndPracticeRatingIsNullAndTopTalentExcelVersion(String practice, TopTalentExcelVersion topTalentExcelVersion);

    List<TopTalentEmployee> findAllByTopTalentExcelVersionUploadedYearAndTopTalentExcelVersionVersionName(String year, String versionName);

    List<TopTalentEmployee> findAllByTopTalentExcelVersionUploadedYear(String year);

    List<TopTalentEmployee> findAllByCompetencyPracticeAndPracticeRatingIsNull(String practice);

    List<TopTalentEmployee> findAllByTopTalentExcelVersionFileName(String fileName);

    List<TopTalentEmployee> findAllByTopTalentExcelVersionAndIsStepUser(TopTalentExcelVersion topTalentExcelVersion, boolean isStepUserTrue);

    List<TopTalentEmployee> findByTopTalentExcelVersionAndIsStepUser(TopTalentExcelVersion topTalentExcelVersion,Boolean isStepUser);

    List<TopTalentEmployee> findAllByUidInAndTopTalentExcelVersion(List<Long> uidS,TopTalentExcelVersion topTalentExcelVersion);

    List<TopTalentEmployee> findByCompetencyPracticeAndTopTalentExcelVersion(String competencyPractice, TopTalentExcelVersion topTalentExcelVersion);

    Optional<TopTalentEmployee> findByUidAndTopTalentExcelVersion(Long uid, TopTalentExcelVersion latestVersion);

    @Query(value = "SELECT DISTINCT tte.competency_practice FROM step_top_talent_employees tte WHERE tte.top_talent_excel_version_id = :#{#version.id}", nativeQuery = true)
    List<String> findDistinctCompetencyPracticesByVersion(@Param("version") TopTalentExcelVersion version);}
