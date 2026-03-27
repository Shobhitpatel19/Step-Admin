package com.top.talent.management.entity;

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
@Entity
@Audited
@IdClass(EmployeeRatingId.class)
@Table(name = "step_employee_rating")
public class EmployeeRating extends Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 4L;

    @Id
    @Column(nullable = false)
    private Long uid;

    private Double rating;

    @Id
    @ManyToOne
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    @Id
    @ManyToOne
    @JoinColumn(name = "version_id", nullable = false)
    private TopTalentExcelVersion topTalentExcelVersion;
}
