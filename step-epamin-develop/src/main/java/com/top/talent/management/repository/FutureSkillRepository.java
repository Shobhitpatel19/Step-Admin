package com.top.talent.management.repository;

import com.top.talent.management.entity.FutureSkill;
import com.top.talent.management.entity.FutureSkillCategory;
import com.top.talent.management.entity.FutureSkillId;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FutureSkillRepository extends JpaRepository<FutureSkill, FutureSkillId> {
    List<FutureSkill> findByPracticeNameAndTopTalentExcelVersion(
             String practiceName, TopTalentExcelVersion topTalentExcelVersion);

    Optional<FutureSkill> findByPracticeNameAndFutureSkillCategoryAndTopTalentExcelVersionAndIsForAspirationRating(String practiceName, FutureSkillCategory category, TopTalentExcelVersion latestVersion, boolean b);

    boolean existsByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(String practiceName, TopTalentExcelVersion latestVersion, boolean b);

    List<FutureSkill> findByPracticeNameAndTopTalentExcelVersionAndIsForAspirationRating(String practiceName, TopTalentExcelVersion latestVersion, boolean b);
}