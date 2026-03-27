package com.top.talent.management.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AspirationDTO {

    private boolean aspiration1;
    private List<AspirationItemDTO> aspirationList;
    private boolean futureSkillAcknowledged;

}
