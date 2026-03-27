package com.top.talent.management.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "step_practice_email_schedule_log")
@RequiredArgsConstructor
@Audited
@SuperBuilder
@Data
@AllArgsConstructor
public class PracticeEmailScheduleLog extends Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 6L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Practice Email Id cannot be Null")
    private String practiceEmailId;

    @NotNull(message = "Date on which email is sent cannot be Null")
    private LocalDate lastEmailSentDate;

    @NotNull(message = "Number of emails sent cannot be Null")
    private Long noOfEmailsSent;

    @ManyToOne
    @JoinColumn(name = "top_talent_excel_version_id", nullable = false)
    private TopTalentExcelVersion topTalentExcelVersion;

}