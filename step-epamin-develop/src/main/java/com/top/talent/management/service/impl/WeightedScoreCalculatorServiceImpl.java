package com.top.talent.management.service.impl;

import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.service.TopTalentExcelVersionService;
import com.top.talent.management.service.WeightedScoreCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.top.talent.management.constants.NumericConstants.CULTURE_SCORE_WEIGHTAGE;
import static com.top.talent.management.constants.NumericConstants.DELIVERY_TI_SCORE_WEIGHTAGE;
import static com.top.talent.management.constants.NumericConstants.ENGX_SCORE_WEIGHTAGE;
import static com.top.talent.management.constants.NumericConstants.EXTRA_MILE_SCORE_WEIGHTAGE;
import static com.top.talent.management.constants.NumericConstants.PRACTICE_RATING_WEIGHTAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeightedScoreCalculatorServiceImpl implements WeightedScoreCalculatorService {

    private final TopTalentEmployeeRepository topTalentEmployeeRepository;
    private final TopTalentEmployeeMapper topTalentEmployeeMapper;
    private final TopTalentExcelVersionService talentExcelVersionService;

    public List<TopTalentEmployeeDTO> calculateAndAssignWeightedScores() {
        List<TopTalentEmployee> topTalentEmployees=topTalentEmployeeRepository.findAllByTopTalentExcelVersion(talentExcelVersionService.findLatestVersion());
        List<TopTalentEmployee> topTalentEmployeeList = topTalentEmployees
                .stream().toList();

        topTalentEmployeeList.forEach(emp -> emp.setOverallWeightedScoreForMerit(calculateWeightedScore(emp)));

        AtomicLong rank = new AtomicLong(1L);
        List<TopTalentEmployee> sortedTopTalentEmployeeList = topTalentEmployeeList
                .stream()
                .sorted(Comparator.comparingDouble(TopTalentEmployee::getOverallWeightedScoreForMerit).reversed())
                .toList();

        sortedTopTalentEmployeeList.forEach(emp -> emp.setRanking(rank.getAndIncrement()));

        topTalentEmployeeRepository.saveAll(sortedTopTalentEmployeeList);

        return sortedTopTalentEmployeeList.stream().map(topTalentEmployeeMapper::employeeDataToEmployeeDTO).toList();
    }

    private double calculateWeightedScore(TopTalentEmployee employee) {
        Double totalScore = 0D;

        totalScore += CULTURE_SCORE_WEIGHTAGE * getOrZero(employee.getCultureScoreFromFeedback());
        totalScore += DELIVERY_TI_SCORE_WEIGHTAGE * getOrZero(employee.getDeliveryFeedbackTtScore());
        totalScore += PRACTICE_RATING_WEIGHTAGE * getOrZero(employee.getPracticeRating());
        totalScore += ENGX_SCORE_WEIGHTAGE * getOrZero(employee.getContributionEngXCulture());
        totalScore += EXTRA_MILE_SCORE_WEIGHTAGE * getOrZero(employee.getContributionExtraMiles());

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedValue = df.format(totalScore);

        return Double.parseDouble(formattedValue);
    }

    private double getOrZero(Number value) {
        return value == null ? 0D : value.doubleValue();
    }
}
