package com.top.talent.management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRatingId implements Serializable {

    private Long uid;

    private SubCategory subCategory;

    private TopTalentExcelVersion topTalentExcelVersion;
}
