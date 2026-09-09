package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.PlanetDiscoveryRequest;
import com.goomez.CosmosX.dto.PlanetDiscoveryResponse;
import com.goomez.CosmosX.service.ExplorationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExplorationController.class)
class ExplorationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExplorationService service;

    @Test
    @DisplayName("POST /exploration/discover creates planet and returns 201")
    void discover_returnsCreated() throws Exception {
        when(service.discover(any(PlanetDiscoveryRequest.class))).thenReturn(new PlanetDiscoveryResponse(
            5L, "Nebulon-7", 1200, 6, List.of("Crystal", "Titanium"), "2026-09-09T15:00:00"
        ));

        String body = """
            { "name": "Nebulon-7", "distance": 1200, "dangerLevel": 6 }
            """;

        mockMvc.perform(post("/exploration/discover")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.name").value("Nebulon-7"))
            .andExpect(jsonPath("$.resources[0]").value("Crystal"))
            .andExpect(jsonPath("$.discoveredAt").value("2026-09-09T15:00:00"));
    }

    @Test
    @DisplayName("POST /exploration/discover returns 400 for invalid dangerLevel")
    void discover_invalid_returnsBadRequest() throws Exception {
        String body = """
            { "name": "Nebulon-7", "distance": 1200, "dangerLevel": 15 }
            """;

        mockMvc.perform(post("/exploration/discover")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}