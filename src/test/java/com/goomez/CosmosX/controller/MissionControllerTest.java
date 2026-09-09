package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.model.Mission;
import com.goomez.CosmosX.service.MissionService;
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

@WebMvcTest(MissionController.class)
class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MissionService service;

    @Test
    @DisplayName("GET /mission returns list of missions")
    void listAll_returnsMissions() throws Exception {
        when(service.listAll()).thenReturn(List.of(
            new Mission(1, 1, List.of(1L, 2L), "PENDING")
        ));

        mockMvc.perform(get("/mission"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].planetId").value(1))
            .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /mission/{id} returns mission by id")
    void listById_returnsMission() throws Exception {
        when(service.listById(1)).thenReturn(new Mission(1, 1, List.of(1L, 2L), "PENDING"));

        mockMvc.perform(get("/mission/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /mission creates mission with PENDING status and returns 201")
    void create_returnsCreated() throws Exception {
        when(service.add(any(Mission.class)))
            .thenReturn(new Mission(2, 1, List.of(1L, 2L), "PENDING"));

        String body = """
            { "planetId": 1, "astronauts": [1, 2] }
            """;

        mockMvc.perform(post("/mission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /mission returns 400 for empty astronauts")
    void create_invalid_returnsBadRequest() throws Exception {
        String body = """
            { "planetId": 1, "astronauts": [] }
            """;

        mockMvc.perform(post("/mission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /mission/{id} updates mission")
    void update_returnsOk() throws Exception {
        when(service.update(any(Long.class), any(Mission.class)))
            .thenReturn(new Mission(1, 2, List.of(1L, 3L), "PENDING"));

        String body = """
            { "planetId": 2, "astronauts": [1, 3], "status": "PENDING" }
            """;

        mockMvc.perform(put("/mission/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.planetId").value(2));
    }

    @Test
    @DisplayName("DELETE /mission/{id} returns 204")
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/mission/1"))
            .andExpect(status().isNoContent());
    }
}