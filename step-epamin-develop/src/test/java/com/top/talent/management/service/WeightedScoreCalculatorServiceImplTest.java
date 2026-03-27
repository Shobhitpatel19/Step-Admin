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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
        MockitoAnnotations.openMocks(this);

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


        when(topTalentEmployeeRepository.findAllByTopTalentExcelVersion(any()))
                .thenReturn(employees);


        double calculatedScore = 4 * 0.35 + 3.8 * 0.30 + 4 * 0.10 + 4 * 0.05 + 3 * 0.20;

        when(topTalentEmployeeMapper.employeeDataToEmployeeDTO(any()))
                .thenReturn(new TopTalentEmployeeDTO(
                        "John Doe", 653006L, "New York", "2020-01-15", "3 Years", "Software Engineer",
                        "Active", "Development", "Engineering", "Jane Smith", "PGM123",
                        "PRJ456", "JF5", "Java Practice", "Java", "Spring, Hibernate",
                        "Yes", "High Performer", "Top Performer", 95D,
                        4.5, 10L, 15L, 80D, calculatedScore, null,
                        "98th Percentile", "HRBP1", "John DH", false
                ));


        List<TopTalentEmployeeDTO> topTalentEmployeeDTOS = weightedScoreCalculatorService.calculateAndAssignWeightedScores();


        Assertions.assertNotNull(topTalentEmployeeDTOS);
        Assertions.assertEquals(1, topTalentEmployeeDTOS.size());

        Assertions.assertEquals(calculatedScore, topTalentEmployeeDTOS.get(0).getOverallWeightedScoreForMerit(), 0.01);

        verify(topTalentEmployeeRepository, times(1)).saveAll(anyList());
    }


}