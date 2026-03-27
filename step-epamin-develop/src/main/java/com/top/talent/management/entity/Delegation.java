package com.top.talent.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity(name = "step_delegation")
@Audited
public class Delegation extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String delegatedBy;

    private String delegatedTo;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "practice_delegation_feature_mapping",
            joinColumns = @JoinColumn(name = "practice_delegation_id"),
            inverseJoinColumns = @JoinColumn(name = "practice_delegation_feature_id")
    )
    private List<PracticeDelegationFeature> practiceDelegationFeatures;

    private Boolean approvalRequired;
}
