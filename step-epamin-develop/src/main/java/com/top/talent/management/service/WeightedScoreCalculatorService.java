package com.top.talent.management.service;

import com.top.talent.management.dto.TopTalentEmployeeDTO;

import java.util.List;


public interface WeightedScoreCalculatorService {
    List<TopTalentEmployeeDTO> calculateAndAssignWeightedScores();
}
