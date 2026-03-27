package com.top.talent.management.repository;

import com.top.talent.management.entity.FutureSkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FutureSkillCategoryRepository extends JpaRepository<FutureSkillCategory, Long> {
    Optional<FutureSkillCategory> findByCategoryName(String categoryName);
}
