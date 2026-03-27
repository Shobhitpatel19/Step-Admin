package com.top.talent.management.entity;

import com.top.talent.management.constants.AspirationPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAspirationId implements Serializable {
    private Long uid;
    private TopTalentExcelVersion topTalentExcelVersion;
    private AspirationPriority priority;
    private AspirationDetail aspirationDetail;

}
