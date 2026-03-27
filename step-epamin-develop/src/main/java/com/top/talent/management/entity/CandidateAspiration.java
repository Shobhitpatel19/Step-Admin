package com.top.talent.management.entity;

import com.top.talent.management.constants.AspirationPriority;
import com.top.talent.management.constants.SubmissionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "step_candidate_aspirations")
@Data
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@IdClass(CandidateAspirationId.class)
@Audited
public class CandidateAspiration extends Auditable{

    @Id
    private Long uid;

    @Id
    @ManyToOne
    @JoinColumn(name = "top_talent_excel_version_id", nullable = false)
    private TopTalentExcelVersion topTalentExcelVersion;

    @Id
    private AspirationPriority priority;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "aspiration_detail_id", nullable = false)
    @Id
    private AspirationDetail aspirationDetail;

    @Column(name = "input_value", columnDefinition = "TEXT")
    private String inputValue;

    @Column(name = "submission_status", nullable = false, length = 1)
    private SubmissionStatus submissionStatus;

    @Column(name = "assigned_role", nullable = true)
    private String assignedRole;

    @Column(name = "proficiency", nullable = true)
    private String proficiency;

    @Column(name = "approved_by", nullable = true)
    private String approvedBy;


    @Column(name = "is_FutureSkillAcknowledgment", nullable = false)
    private boolean futureSkillAcknowledged;

    @Column(name = "is_acknowledgment", nullable = false)
    private boolean submitAcknowledged;
}