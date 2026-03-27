package com.top.talent.management.repository;

import com.top.talent.management.entity.IdentificationClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaAuditing
public interface IdentificationClosureRepository extends JpaRepository<IdentificationClosure, Long> {
}
