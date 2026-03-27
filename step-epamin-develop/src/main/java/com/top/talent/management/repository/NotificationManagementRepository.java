package com.top.talent.management.repository;

import com.top.talent.management.entity.EmailCategories;
import com.top.talent.management.entity.NotificationManagement;
import com.top.talent.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationManagementRepository extends JpaRepository<NotificationManagement, Long> {

    List<NotificationManagement> findByUserUuid(Long userId);
    boolean existsByUserAndCategory(User user, EmailCategories category);
    Optional<NotificationManagement> findByUserUuidAndCategoryCategoryId(Long userUuid, Long categoryId);



}
