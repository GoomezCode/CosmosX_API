package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.StatsResponse;
import com.goomez.CosmosX.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatsService service;

    @Test
    @DisplayName("GET /stats returns aggregated statistics")
    void getStats_returnsStats() throws Exception {
        when(service.getStats()).thenReturn(new StatsResponse(2, 2, 0, 100.0, 75, "Iron"));

        mockMvc.perform(get("/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalMissions").value(2))
            .andExpect(jsonPath("$.successes").value(2))
            .andExpect(jsonPath("$.failures").value(0))
            .andExpect(jsonPath("$.successRate").value(100.0))
            .andExpect(jsonPath("$.resourcesCollected").value(75))
            .andExpect(jsonPath("$.topResource").value("Iron"));
    }
}