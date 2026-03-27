package com.top.talent.management.repository;

import com.top.talent.management.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaAuditing
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
