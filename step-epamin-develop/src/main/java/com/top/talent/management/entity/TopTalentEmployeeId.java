package com.top.talent.management.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopTalentEmployeeId implements Serializable {

    private Long uid;
    private TopTalentExcelVersion topTalentExcelVersion;
}

