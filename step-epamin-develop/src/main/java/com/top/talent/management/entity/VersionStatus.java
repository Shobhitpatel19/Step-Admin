package com.top.talent.management.entity;

import com.top.talent.management.constants.SubmissionStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Audited
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "step_version_status")
public class VersionStatus extends Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 12L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long filterId;


    private SubmissionStatus submissionStatus;

    @OneToOne
    private TopTalentExcelVersion topTalentExcelVersion;

}
