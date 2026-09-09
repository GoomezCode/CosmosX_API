package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.service.AstronautService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AstronautController.class)
class AstronautControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AstronautService service;

    @Test
    @DisplayName("GET /astronauts returns list of astronauts")
    void listAll_returnsAstronauts() throws Exception {
        when(service.listAll()).thenReturn(List.of(
            new Astronaut(1, "Daniel", "Commander", 1200)
        ));

        mockMvc.perform(get("/astronauts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Daniel"))
            .andExpect(jsonPath("$[0].rank").value("Commander"))
            .andExpect(jsonPath("$[0].experience").value(1200));
    }

    @Test
    @DisplayName("GET /astronauts/{id} returns astronaut by id")
    void listById_returnsAstronaut() throws Exception {
        when(service.listById(1)).thenReturn(new Astronaut(1, "Daniel", "Commander", 1200));

        mockMvc.perform(get("/astronauts/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Daniel"));
    }

    @Test
    @DisplayName("GET /astronauts/{id} returns 404 for unknown id")
    void listById_unknownId_returnsNotFound() throws Exception {
        when(service.listById(999)).thenThrow(new ResourceNotFoundException("Astronaut not found with id: 999"));

        mockMvc.perform(get("/astronauts/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /astronauts creates astronaut and returns 201")
    void create_returnsCreated() throws Exception {
        when(service.add(any(Astronaut.class)))
            .thenReturn(new Astronaut(2, "Laura", "Pilot", 800));

        String body = """
            { "name": "Laura", "rank": "Pilot", "experience": 800 }
            """;

        mockMvc.perform(post("/astronauts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Laura"));
    }

    @Test
    @DisplayName("POST /astronauts returns 400 for blank fields")
    void create_invalid_returnsBadRequest() throws Exception {
        String body = """
            { "name": "", "rank": "", "experience": -5 }
            """;

        mockMvc.perform(post("/astronauts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PUT /astronauts/{id} updates astronaut")
    void update_returnsOk() throws Exception {
        when(service.update(any(Long.class), any(Astronaut.class)))
            .thenReturn(new Astronaut(1, "Daniel", "Captain", 1500));

        String body = """
            { "name": "Daniel", "rank": "Captain", "experience": 1500 }
            """;

        mockMvc.perform(put("/astronauts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rank").value("Captain"));
    }

    @Test
    @DisplayName("DELETE /astronauts/{id} returns 204")
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/astronauts/1"))
            .andExpect(status().isNoContent());
    }
}