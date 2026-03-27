package com.top.talent.management.repository;

import com.top.talent.management.entity.EmployeeRating;
import com.top.talent.management.entity.EmployeeRatingId;
import com.top.talent.management.entity.SubCategory;
import com.top.talent.management.entity.TopTalentExcelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaAuditing
public interface PracticeRatingRepository extends JpaRepository<EmployeeRating, EmployeeRatingId> {

    List<EmployeeRating> findByUid(Long uid);

    Optional<EmployeeRating> findByUidAndSubCategoryAndTopTalentExcelVersion(Long uid, SubCategory subCategory, TopTalentExcelVersion topTalentExcelVersion);

    Optional<List<EmployeeRating>> findEmployeeRatingByUid(Long uid);

    List<EmployeeRating> findByUidAndTopTalentExcelVersion(Long uid, TopTalentExcelVersion topTalentExcelVersion);
}
