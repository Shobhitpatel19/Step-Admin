package com.top.talent.management.controller;

import com.top.talent.management.dto.ExcelFileDetailsDTO;
import com.top.talent.management.service.CulturalScoreService;
import com.top.talent.management.service.EngXExtraMileRatingService;
import com.top.talent.management.service.impl.ExcelFileDetailsServiceImpl;
import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.service.MasterDataService;
import com.top.talent.management.service.TopTalentEmployeeService;
import com.top.talent.management.service.WeightedScoreCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TopTalentEmployeeController.class)
public class FileDetailsControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopTalentEmployeeService topTalentEmployeeService;

    @MockBean
    private WeightedScoreCalculatorService weightedScoreCalculatorService;

    @MockBean
    private MasterDataService masterDataService;

    @MockBean
    private CulturalScoreService culturalScoreService;
    @MockBean
    private EngXExtraMileRatingService engXExtraMileRatingService;
    @MockBean
    private JwtUtilService jwtUtilService;


    @MockBean
    private ExcelFileDetailsServiceImpl fileDetailsService;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetFileDetails() throws Exception {
        ExcelFileDetailsDTO mockDetails = new ExcelFileDetailsDTO();
        mockDetails.setExcelName("example");
        mockDetails.setHeaders(List.of("Header1", "Header2"));

        given(fileDetailsService.generateExcelFile("step")).willReturn(mockDetails);

        mockMvc.perform(get("/step/file-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("excelType", "step"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.excelName").value("example"))
                .andExpect(jsonPath("$.headers").isArray())
                .andExpect(jsonPath("$.headers[0]").value("Header1"))
                .andExpect(jsonPath("$.headers[1]").value("Header2"));
    }
}