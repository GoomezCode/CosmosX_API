package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.model.Spacecraft;
import com.goomez.CosmosX.service.SpacecraftService;
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

@WebMvcTest(SpacecraftController.class)
class SpacecraftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpacecraftService service;

    @Test
    @DisplayName("GET /spacecraft returns list of spacecraft")
    void listAll_returnsSpacecraft() throws Exception {
        when(service.listAll()).thenReturn(List.of(
            new Spacecraft(1, "Falcon-X", 1000, 5, "READY")
        ));

        mockMvc.perform(get("/spacecraft"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Falcon-X"))
            .andExpect(jsonPath("$[0].fuel").value(1000));
    }

    @Test
    @DisplayName("GET /spacecraft/{id} returns spacecraft by id")
    void listById_returnsSpacecraft() throws Exception {
        when(service.listById(1)).thenReturn(new Spacecraft(1, "Falcon-X", 1000, 5, "READY"));

        mockMvc.perform(get("/spacecraft/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    @DisplayName("POST /spacecraft creates spacecraft and returns 201")
    void create_returnsCreated() throws Exception {
        when(service.add(any(Spacecraft.class)))
            .thenReturn(new Spacecraft(2, "Apollo-7", 800, 3, "READY"));

        String body = """
            { "name": "Apollo-7", "fuel": 800, "capacity": 3, "status": "READY" }
            """;

        mockMvc.perform(post("/spacecraft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @DisplayName("POST /spacecraft returns 400 for capacity < 1")
    void create_invalid_returnsBadRequest() throws Exception {
        String body = """
            { "name": "Apollo-7", "fuel": 800, "capacity": 0, "status": "READY" }
            """;

        mockMvc.perform(post("/spacecraft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /spacecraft/{id} updates spacecraft")
    void update_returnsOk() throws Exception {
        when(service.update(any(Long.class), any(Spacecraft.class)))
            .thenReturn(new Spacecraft(1, "Falcon-X", 500, 5, "MAINTENANCE"));

        String body = """
            { "name": "Falcon-X", "fuel": 500, "capacity": 5, "status": "MAINTENANCE" }
            """;

        mockMvc.perform(put("/spacecraft/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("MAINTENANCE"));
    }

    @Test
    @DisplayName("DELETE /spacecraft/{id} returns 204")
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/spacecraft/1"))
            .andExpect(status().isNoContent());
    }
}