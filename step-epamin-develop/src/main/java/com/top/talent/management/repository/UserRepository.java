package com.top.talent.management.repository;

import com.top.talent.management.entity.Role;
import com.top.talent.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaAuditing
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByPractice(String practice);

    List<User> findUserByPracticeAndRole(String practice, Role role);
    List<User> findAllByRole(Role role);

    List<User> findAllByRoleAndStatus(Role superAdminRole, String status);

    List <User> findAllByRoleAndStatusAndPractice(Role superAdminRole, String status, String practice);

    Optional<User> findByPracticeAndRoleAndIsDelegate(String practice, Role role, Boolean isDelegate);
    List<User> findByRoleId(int roleId);

}
