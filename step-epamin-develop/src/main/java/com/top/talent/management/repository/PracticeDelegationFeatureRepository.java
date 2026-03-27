package com.top.talent.management.repository;

import com.top.talent.management.entity.PracticeDelegationFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaAuditing
public interface PracticeDelegationFeatureRepository extends JpaRepository<PracticeDelegationFeature,Long> {
    List<PracticeDelegationFeature> findByNameIn(List<String> names);

    PracticeDelegationFeature findByName(String name);
}

