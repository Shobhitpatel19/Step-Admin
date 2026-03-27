package com.top.talent.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;


@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Audited
@Table(name = "step_email_schedule")

public class EmailSchedule extends Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date on which email should be sent cannot be Null")
    private LocalDate date;

    @NotNull(message = "Email Template cannot be Null")
    private String emailTemplate;

    @NotNull(message = "Email Subject cannot be null")
    private String emailSubject;

    @NotNull(message = "Is First Date cannot be Null")
    private Boolean isFirstDate;

    @NotNull(message = "Is Last Date cannot be Null")
    private Boolean hasMailSent;

    @ManyToOne
    @JoinColumn(name = "top_talent_excel_version_id", nullable = false)
    private TopTalentExcelVersion topTalentExcelVersion;

}
