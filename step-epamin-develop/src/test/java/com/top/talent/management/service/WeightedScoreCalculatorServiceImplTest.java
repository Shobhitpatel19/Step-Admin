package com.top.talent.management.service;

import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.entity.TopTalentEmployee;
import com.top.talent.management.entity.TopTalentExcelVersion;
import com.top.talent.management.mapper.TopTalentEmployeeMapper;
import com.top.talent.management.repository.TopTalentEmployeeRepository;
import com.top.talent.management.service.impl.WeightedScoreCalculatorServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Collections;
import java.util.List;

import static com.top.talent.management.constants.NumericConstants.CULTURE_SCORE_WEIGHTAGE;
import static com.top.talent.management.constants.NumericConstants.DELIVERY_TI_SCORE_WEIGHTAGE;
import static com.top.talent.management.constants.NumericConstants.ENGX_SCORE_WEIGHTAGE;
import static com.top.talent.management.constants.NumericConstants.EXTRA_MILE_SCORE_WEIGHTAGE;
import static com.top.talent.management.constants.NumericConstants.PRACTICE_RATING_WEIGHTAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class WeightedScoreCalculatorServiceImplTest {

    @Mock
    private TopTalentEmployeeRepository topTalentEmployeeRepository;
    @Mock
    private TopTalentEmployeeMapper topTalentEmployeeMapper;
    @Mock
    private TopTalentExcelVersionService talentExcelVersionService;
    @InjectMocks
    private WeightedScoreCalculatorServiceImpl weightedScoreCalculatorService;

    private TopTalentEmployee employee1;

    @BeforeEach
    void setUp() {
        TopTalentExcelVersion latestVersion = new TopTalentExcelVersion(); // Assuming constructor
        latestVersion.setId(1L); // Set required properties as needed

        employee1 = TopTalentEmployee.builder()
                .uid(101L)
                .topTalentExcelVersion(latestVersion)
                .name("John Doe")
                .email("john.doe@example.com")
                .location("New York")
                .doj("2020-01-01")
                .timeWithEPAM("2 years")
                .title("Software Engineer")
                .status("Active")
                .productionCategory("Development")
                .resourceManager("Manager A")
                .jfLevel("Level 2")
                .competencyPractice("Backend")
                .primarySkill("Java")
                .talentProfile("Gold")
                .deliveryFeedbackTtScore(9.0)
                .practiceRating(8.5)
                .contributionEngXCulture(9L)
                .contributionExtraMiles(7L)
                .cultureScoreFromFeedback(8.0)
                .build();


    }

    @Test
   void testCalculateAndAssignWeightedScores_WhenAllFieldsPresent() {

        TopTalentExcelVersion mockExcelVersion = new TopTalentExcelVersion();
        mockExcelVersion.setFileName("STEP_2025_V1");
        mockExcelVersion.setVersionName("V1");
        mockExcelVersion.setUploadedYear("2025");


        employee1.setTopTalentExcelVersion(mockExcelVersion);
        List<TopTalentEmployee> employees = Collections.singletonList(employee1);

        when(talentExcelVersionService.findLatestVersion())
                .thenReturn(mockExcelVersion);

        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(mockExcelVersion))
                .thenReturn(employees);


        Assertions.assertAll(
                () -> Assertions.assertEquals(0.35, PRACTICE_RATING_WEIGHTAGE, 0.0),
                () -> Assertions.assertEquals(0.20, ENGX_SCORE_WEIGHTAGE, 0.0),
                () -> Assertions.assertEquals(0.05, EXTRA_MILE_SCORE_WEIGHTAGE, 0.0),
                () -> Assertions.assertEquals(0.20, CULTURE_SCORE_WEIGHTAGE, 0.0),
                () -> Assertions.assertEquals(0.20, DELIVERY_TI_SCORE_WEIGHTAGE, 0.0),
                () -> Assertions.assertEquals(1.0,
                        PRACTICE_RATING_WEIGHTAGE + ENGX_SCORE_WEIGHTAGE + EXTRA_MILE_SCORE_WEIGHTAGE
                                + CULTURE_SCORE_WEIGHTAGE + DELIVERY_TI_SCORE_WEIGHTAGE,
                        0.0)
        );

        double calculatedScore = 8.53;

        when(topTalentEmployeeMapper.employeeDataToEmployeeDTO(any()))
                .thenAnswer(invocation -> {
                    TopTalentEmployee employee = invocation.getArgument(0);
                    TopTalentEmployeeDTO dto = new TopTalentEmployeeDTO();
                    dto.setOverallWeightedScoreForMerit(employee.getOverallWeightedScoreForMerit());
                    return dto;
                });


        List<TopTalentEmployeeDTO> topTalentEmployeeDTOS = weightedScoreCalculatorService.calculateAndAssignWeightedScores();


        Assertions.assertNotNull(topTalentEmployeeDTOS);
        Assertions.assertEquals(1, topTalentEmployeeDTOS.size());

        Assertions.assertEquals(calculatedScore, topTalentEmployeeDTOS.get(0).getOverallWeightedScoreForMerit(), 0.01);

        verify(topTalentEmployeeRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testCalculateAndAssignWeightedScores_IncludesDeliveryFeedbackAtTwentyPercent() {
        TopTalentExcelVersion mockExcelVersion = new TopTalentExcelVersion();
        mockExcelVersion.setFileName("STEP_2025_V1");
        mockExcelVersion.setVersionName("V1");
        mockExcelVersion.setUploadedYear("2025");

        TopTalentEmployee deliveryOnlyEmployee = TopTalentEmployee.builder()
                .uid(102L)
                .topTalentExcelVersion(mockExcelVersion)
                .name("Jane Doe")
                .deliveryFeedbackTtScore(10.0)
                .practiceRating(0.0)
                .contributionEngXCulture(0L)
                .contributionExtraMiles(0L)
                .cultureScoreFromFeedback(0.0)
                .build();

        when(talentExcelVersionService.findLatestVersion())
                .thenReturn(mockExcelVersion);
        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(mockExcelVersion))
                .thenReturn(Collections.singletonList(deliveryOnlyEmployee));
        when(topTalentEmployeeMapper.employeeDataToEmployeeDTO(any()))
                .thenAnswer(invocation -> {
                    TopTalentEmployee employee = invocation.getArgument(0);
                    TopTalentEmployeeDTO dto = new TopTalentEmployeeDTO();
                    dto.setOverallWeightedScoreForMerit(employee.getOverallWeightedScoreForMerit());
                    return dto;
                });

        List<TopTalentEmployeeDTO> result = weightedScoreCalculatorService.calculateAndAssignWeightedScores();

        Assertions.assertEquals(2.0, result.get(0).getOverallWeightedScoreForMerit(), 0.01);
    }


}