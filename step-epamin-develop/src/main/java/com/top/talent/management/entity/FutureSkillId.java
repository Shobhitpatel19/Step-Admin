package com.top.talent.management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FutureSkillId implements Serializable {
    private String practiceName;
    private FutureSkillCategory futureSkillCategory;
    private TopTalentExcelVersion topTalentExcelVersion;
    private Boolean isForAspirationRating;
}
