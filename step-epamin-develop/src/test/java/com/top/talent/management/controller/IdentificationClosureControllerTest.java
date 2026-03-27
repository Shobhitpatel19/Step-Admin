package com.top.talent.management.controller;

import com.top.talent.management.service.impl.JwtUtilService;
import com.top.talent.management.service.IdentificationClosureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(IdentificationClosureController.class)
class IdentificationClosureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdentificationClosureService identificationClosureService;




    @MockBean
    private JwtUtilService jwtUtilService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new IdentificationClosureController(identificationClosureService)).build();
    }

    @Test
    void shouldReturnPhaseClosedWhenServiceReturnsTrue() throws Exception {
        when(identificationClosureService.isPhaseClosed()).thenReturn(true);

        mockMvc.perform(get("/step/identification/isended")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"status\":true}"));
    }

    @Test
    void shouldReturnPhaseNotClosedWhenServiceReturnsFalse() throws Exception {
        when(identificationClosureService.isPhaseClosed()).thenReturn(false);

        mockMvc.perform(get("/step/identification/isended")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"status\":false}"));
    }

}