package com.top.talent.management.repository;


import com.top.talent.management.entity.Delegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaAuditing
public interface PracticeDelegationRepository extends JpaRepository<Delegation,Long> {
    Delegation findByDelegatedBy(String email);
    Delegation findByDelegatedTo(String email);
}
