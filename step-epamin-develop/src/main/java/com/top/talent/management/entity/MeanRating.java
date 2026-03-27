package com.top.talent.management.entity;

import com.top.talent.management.constants.SubmissionStatus;
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

import java.io.Serial;
import java.io.Serializable;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Audited
@Entity
@IdClass(MeanRatingId.class)
@Table(name = "step_mean_rating")
public class MeanRating extends Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 5L;

    @Id
    @Column(nullable = false)
    private Long uid;

    @Column(nullable = false)
    private Double mean;

    @Column(nullable = false)
    private SubmissionStatus submissionStatus;

    @Id
    @ManyToOne
    @JoinColumn(name = "version_id", nullable = false)
    private TopTalentExcelVersion topTalentExcelVersion;
}
