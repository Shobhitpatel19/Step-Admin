package com.top.talent.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopTalentEmployeeDTO {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("UID")
    private Long uid;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("DOJ")
    private String doj;

    @JsonProperty("Time With EPAM")
    private String timeWithEPAM;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Production Category")
    private String productionCategory;

    @JsonProperty("Job Function")
    private String jobFunction;

    @JsonProperty("Resource Manager")
    private String resourceManager;

    @JsonProperty("PGM")
    private String pgm;

    @JsonProperty("Project Code")
    private String projectCode;

    @JsonProperty("JF Level")
    private String jfLevel;

    @JsonProperty("Competency Practice")
    private String competencyPractice;

    @JsonProperty("Primary Skill")
    private String primarySkill;

    @JsonProperty("Niche Skills")
    private String nicheSkills;

    @JsonProperty("Niche Skill Yes/No")
    private String nicheSkillYesNo;

    @JsonProperty("Talent Profile Previous Year")
    private String talentProfilePreviousYear;

    @JsonProperty("Talent Profile")
    private String talentProfile;

    @JsonProperty("Delivery Feedback TT Score")
    private Double deliveryFeedbackTtScore;

    @JsonProperty("Practice Rating")
    private Double practiceRating;

    @JsonProperty("Contribution EngX Culture")
    private Long contributionEngXCulture;

    @JsonProperty("Contribution Extra Miles")
    private Long contributionExtraMiles;

    @JsonProperty("Culture Score from Feedback")
    private Double cultureScoreFromFeedback;

    @JsonProperty("Overall Weighted Score for Merit")
    private Double overallWeightedScoreForMerit;

    @JsonProperty("Ranking")
    private Long ranking;

    @JsonProperty("Percentile")
    private String percentile;

    @JsonProperty("HRBP Mapping")
    private String hrbpMapping;

    @JsonProperty("DH")
    private String dh;

    @JsonProperty("Is Step User")
    private boolean isStepUser;
}