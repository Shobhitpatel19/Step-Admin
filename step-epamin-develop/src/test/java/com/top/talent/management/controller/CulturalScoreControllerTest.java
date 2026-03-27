package com.top.talent.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.top.talent.management.constants.RoleConstants;
import com.top.talent.management.dto.TopTalentEmployeeDTO;
import com.top.talent.management.exception.EmptyFileException;
import com.top.talent.management.security.CustomUserPrincipal;
import com.top.talent.management.service.*;
import com.top.talent.management.service.impl.ExcelFileDetailsServiceImpl;
import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.utils.CulturalScoreTestUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.top.talent.management.utils.TestUtils.getMockAuthenticationWithSecurity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(SpringExtension.class)
@WebMvcTest(value = TopTalentEmployeeController.class)
class CulturalScoreControllerTest {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopTalentEmployeeService topTalentEmployeeService;


    @MockBean
    private CulturalScoreService culturalScoreService;
    @MockBean
    private EngXExtraMileRatingService engXExtraMileRatingService;
    @MockBean
    private JwtUtilService jwtUtilService;

    @MockBean
    private MasterDataService masterDataService;

    @MockBean
    private WeightedScoreCalculatorService weightedScoreCalculatorService;

    @MockBean
    private ExcelFileDetailsServiceImpl generateExcelFileDetailsService;

    @Test
    void testCulturalScoreUploadForbidden() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_USER);

        mockMvc.perform(multipart("/step/cultural-score/upload")
                        .file(CulturalScoreTestUtils.createValidFile("STEP_CULTURAL_SCORE_2024_1.xlsx"))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertEquals("{\"errorMessage\":\"You do not have permission to access this resource.\",\"errors\":null}", result.getResponse().getContentAsString()));
    }

    @Test
    void testCulturalScoreUploadSuccess() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        MockMultipartFile file = CulturalScoreTestUtils.createValidFile("STEP_CULTURAL_SCORE_2024_V2.xlsx");

        List<TopTalentEmployeeDTO> employeeDTOS = Collections.singletonList(new TopTalentEmployeeDTO());

        when(culturalScoreService.parseAndSaveCulturalScore(file, (CustomUserPrincipal) authentication.getPrincipal())).thenReturn(employeeDTOS);

        mockMvc.perform(
                        multipart("/step/cultural-score/upload")
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .principal(authentication)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(employeeDTOS)));
    }

    @Test
    void testCulturalScoreUploadEmptyFileException() throws Exception {
        Authentication authentication = getMockAuthenticationWithSecurity(RoleConstants.ROLE_SUPER_ADMIN);

        MockMultipartFile file = CulturalScoreTestUtils.createEmptyFile("STEP_CULTURAL_SCORE_2024_V2.xlsx");

        when(culturalScoreService.parseAndSaveCulturalScore(file, (CustomUserPrincipal) authentication.getPrincipal()))
                .thenThrow(new EmptyFileException("File is empty"));

        mockMvc.perform(multipart("/step/cultural-score/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .principal(authentication)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
