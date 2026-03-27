package com.top.talent.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity(name = "step_practice_delegation_feature")
@Audited
public class PracticeDelegationFeature extends Auditable{

    @Serial
    private static final long serialVersionUID = 14L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String frontendPath;

    @ManyToMany(mappedBy = "practiceDelegationFeatures")
    private List<Delegation> delegations;

}
