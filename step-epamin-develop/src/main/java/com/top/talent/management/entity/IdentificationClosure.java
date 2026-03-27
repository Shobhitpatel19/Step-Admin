package com.top.talent.management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * Entity representing the closure of an identification step,
 * when the identification phase has ended and the step users have been finalized.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "step_identification_closure")
@Audited
public class IdentificationClosure {

    /**
     * Unique identifier for the IdentificationClosure entity.
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /**
     * The version of the Excel document used in the talent identification phase.
     * This is a one-to-one relationship between IdentificationClosure and TopTalentExcelVersion.
     */
    @OneToOne
    private TopTalentExcelVersion topTalentExcelVersion;

    /**
     * Username of the user who ended the identification process.
     * This field is automatically populated based on the currently authenticated user.
     */
    @CreatedBy
    private String endedBy;

    /**
     * Timestamp indicating when the identification process was ended.
     * This field is automatically set to the current date and time at the moment of entity creation.
     */
    @CreatedDate
    private LocalDateTime endedAt;
}