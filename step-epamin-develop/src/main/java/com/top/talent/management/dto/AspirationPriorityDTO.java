package com.top.talent.management.dto;

import com.top.talent.management.constants.AspirationPriority;
import com.top.talent.management.constants.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AspirationPriorityDTO {

    private AspirationPriority priority;

    private List<AspirationItemDTO> aspirationList;

    private SubmissionStatus submissionStatus;

}
