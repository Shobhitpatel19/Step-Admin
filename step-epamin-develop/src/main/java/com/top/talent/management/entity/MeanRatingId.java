package com.top.talent.management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeanRatingId implements Serializable {

    private Long uid;

    private TopTalentExcelVersion topTalentExcelVersion;
}
