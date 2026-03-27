package com.top.talent.management.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;

@Entity(name = "step_top_talent_employees")
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@IdClass(TopTalentEmployeeId.class)
public class TopTalentEmployee extends Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 9L;

    @NotNull
    @ExcelProperty("NAME")
    private String name;

    @Id
    @ExcelProperty("UID")
    @NotNull
    private Long uid;

    @Id
    @ManyToOne
    @ExcelIgnore
    @JoinColumn(name = "top_talent_excel_version_id", nullable = false)
    private TopTalentExcelVersion topTalentExcelVersion;

    @NotNull
    @ExcelProperty("Email")
    private String email;

    @ExcelProperty("Location")
    private String location;

    @NotNull
    @ExcelProperty("DOJ")
    private String doj;

    @ExcelProperty("Time with Epam")
    private String timeWithEPAM;

    @NotNull
    @ExcelProperty("TITLE")
    private String title;

    @ExcelProperty("STATUS")
    private String status;

    @ExcelProperty("PRODUCTION CATEGORY")
    private String productionCategory;

    @ExcelProperty("JOB FUNCTION")
    private String jobFunction;

    @NotNull
    @ExcelProperty("RESOURCE MANAGER")
    private String resourceManager;

    @ExcelProperty("PGM")
    private String pgm;

    @ExcelProperty("PROJECT CODE")
    private String projectCode;

    @NotNull
    @ExcelProperty("JF_LEVEL")
    private String jfLevel;

    @NotNull
    @ExcelProperty("Competency /Practice")
    private String competencyPractice;

    @NotNull
    @ExcelProperty("Primary Skill")
    private String primarySkill;

    @ExcelProperty("Niche Skills")
    private String nicheSkills;

    @ExcelProperty("Niche Skill(Yes/No)")
    private String nicheSkillYesNo;

    @ExcelProperty("Talent Profile previous year")
    private String talentProfilePreviousYear;

    @NotNull
    @ExcelProperty("Talent Profile current year")
    private String talentProfile;

    @NotNull
    @ExcelProperty("Delivery Feedback TT Score")
    private Double deliveryFeedbackTtScore;

    private Double practiceRating;

    private Long contributionEngXCulture;

    private Long contributionExtraMiles;

    private Double cultureScoreFromFeedback;

    private Double overallWeightedScoreForMerit;

    private Long ranking;

    private String percentile;

    private String hrbpMapping;

    private String dh;

    private Boolean isStepUser = false;
}
