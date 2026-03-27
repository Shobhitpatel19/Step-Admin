package com.top.talent.management.dto;

import com.top.talent.management.constants.RatingStatus;
import com.top.talent.management.constants.SubmissionStatus;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MasterDataResponseDTO {

    private RatingStatus ratingStatus;
    private SubmissionStatus submissionStatus;
    private List<TopTalentExcelVersionDTO> topTalentExcelVersions;
    private Map<String, Map<String, List<String>>> practiceHeadListDetailed;
    private TopTalentExcelVersionDTO fetchedExcelVersion;
    private List<TopTalentEmployeeDTO> topTalentEmployeeDTOList;
    private List<TopTalentEmployeeDTO> filteredTopTalentEmployees;
    private Long noOfExcelVersion;

}
