package com.top.talent.management.repository;
import com.top.talent.management.entity.EmailSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@EnableJpaAuditing
public interface EmailScheduleRepository extends JpaRepository<EmailSchedule, Long> {

    Boolean existsByDate(LocalDate date);
    EmailSchedule findByDate(LocalDate date);
    Boolean existsByDateAndHasMailSent(LocalDate date, Boolean bool);
}