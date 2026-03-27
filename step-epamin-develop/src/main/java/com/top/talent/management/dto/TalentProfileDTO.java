package com.top.talent.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentProfileDTO {

    private Long uid;
    private String talentProfileCurrentYear;
    private String talentProfilePreviousYear;
    private Integer year;

}
