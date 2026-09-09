package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.service.PlanetService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanetController.class)
class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanetService service;

    @Test
    @DisplayName("GET /planet returns list of planets")
    void listAll_returnsPlanets() throws Exception {
        when(service.listAll()).thenReturn(List.of(
            new Planet(1, "Zorion", 500, 3, List.of("Iron", "Water"))
        ));

        mockMvc.perform(get("/planet"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Zorion"))
            .andExpect(jsonPath("$[0].dangerLevel").value(3));
    }

    @Test
    @DisplayName("GET /planet/{id} returns planet by id")
    void listById_returnsPlanet() throws Exception {
        when(service.listById(1)).thenReturn(new Planet(1, "Zorion", 500, 3, List.of("Iron")));

        mockMvc.perform(get("/planet/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.distance").value(500));
    }

    @Test
    @DisplayName("POST /planet creates planet and returns 201")
    void create_returnsCreated() throws Exception {
        when(service.add(any(Planet.class)))
            .thenReturn(new Planet(2, "Mars-X", 500, 4, List.of("Gold")));

        String body = """
            { "name": "Mars-X", "distance": 500, "dangerLevel": 4, "resources": ["Gold"] }
            """;

        mockMvc.perform(post("/planet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @DisplayName("POST /planet returns 400 for blank name")
    void create_invalid_returnsBadRequest() throws Exception {
        String body = """
            { "name": "", "distance": 500, "dangerLevel": 4, "resources": [] }
            """;

        mockMvc.perform(post("/planet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /planet/{id} updates planet")
    void update_returnsOk() throws Exception {
        when(service.update(any(Long.class), any(Planet.class)))
            .thenReturn(new Planet(1, "Zorion", 550, 5, List.of("Gold")));

        String body = """
            { "name": "Zorion", "distance": 550, "dangerLevel": 5, "resources": ["Gold"] }
            """;

        mockMvc.perform(put("/planet/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.distance").value(550));
    }

    @Test
    @DisplayName("DELETE /planet/{id} returns 204")
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/planet/1"))
            .andExpect(status().isNoContent());
    }
}