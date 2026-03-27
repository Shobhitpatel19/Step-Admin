package com.top.talent.management.repository;

import com.top.talent.management.entity.EmailCategories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailCategoriesRepository extends JpaRepository<EmailCategories,Long> {
    Optional<EmailCategories> findByName(String name);

}
