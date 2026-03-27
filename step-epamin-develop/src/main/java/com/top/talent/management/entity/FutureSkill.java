package com.top.talent.management.entity;

import com.top.talent.management.constants.SubmissionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@Audited
@IdClass(FutureSkillId.class)
@Entity
@Table(name = "step_future_skills")
@NoArgsConstructor
public class FutureSkill extends Auditable{

        @Id
        @NotBlank(message = "Practice name cannot be blank")
        @Size(max = 100, message = "Practice  name cannot exceed 100 characters")
        @Column(name = "practice_name", nullable = false)
        private String practiceName;

        @Column(name = "submission_status", nullable = false)
        @Enumerated(EnumType.STRING)
        private SubmissionStatus submissionStatus;

        @Id
        @ManyToOne
        @JoinColumn(name = "category_id", nullable = false)
        private FutureSkillCategory futureSkillCategory;

        @Column(name = "answer")
        private String answer;

        @Id
        @ManyToOne
        @JoinColumn(name = "version_id", nullable = false)
        private TopTalentExcelVersion topTalentExcelVersion;

        @Column(name = "is_for_aspiration_rating", nullable = false)
        private boolean isForAspirationRating;

    }

