package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.AstronautRankingResponse;
import com.goomez.CosmosX.service.RankingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RankingController.class)
class RankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RankingService service;

    @Test
    @DisplayName("GET /ranking returns astronauts ordered by experience")
    void getRanking_returnsRanking() throws Exception {
        when(service.getRanking()).thenReturn(List.of(
            new AstronautRankingResponse("Daniel", "Commander", 5200, 15),
            new AstronautRankingResponse("Laura", "Pilot", 3800, 12)
        ));

        mockMvc.perform(get("/ranking"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Daniel"))
            .andExpect(jsonPath("$[0].rank").value("Commander"))
            .andExpect(jsonPath("$[0].experience").value(5200))
            .andExpect(jsonPath("$[0].missionsCompleted").value(15))
            .andExpect(jsonPath("$[1].name").value("Laura"))
            .andExpect(jsonPath("$[1].missionsCompleted").value(12));
    }
}