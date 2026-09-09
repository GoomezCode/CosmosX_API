package com.goomez.CosmosX.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CosmosXE2eTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("E2E: astronauta -> nave -> planeta -> missao -> execucao -> historico/stats/ranking")
    void fullMissionFlow() throws Exception {
        long astronautId = createAstronaut("E2E Pilot", "Captain", 100);
        long spacecraftId = createSpacecraft("E2E-Ship", 100000, 4, "READY");
        long planetId = createPlanet("E2E-Planet-1", 200, 3, "[\"Iron\", \"Water\"]");
        long missionId = createMission(spacecraftId, planetId, astronautId);

        mockMvc.perform(get("/mission/" + missionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.spacecraftId").value(spacecraftId))
            .andExpect(jsonPath("$.planetId").value(planetId))
            .andExpect(jsonPath("$.astronauts[0]").value(astronautId));

        MvcResult execution = mockMvc.perform(post("/mission/" + missionId + "/start"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.missionId").value(missionId))
            .andExpect(jsonPath("$.status", anyOf(is("SUCCESS"), is("FAILED"))))
            .andExpect(jsonPath("$.fuelConsumed").value(600))
            .andExpect(jsonPath("$.events[0]").exists())
            .andReturn();

        JsonNode executionBody = objectMapper.readTree(execution.getResponse().getContentAsString());
        String missionStatus = executionBody.path("status").asText();
        if ("SUCCESS".equals(missionStatus)) {
            mockMvc.perform(get("/mission/" + missionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
            mockMvc.perform(get("/spacecraft/" + spacecraftId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fuel").value(100000 - 600));
        }

        mockMvc.perform(get("/mission/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.planetName == 'E2E-Planet-1')]").exists());

        mockMvc.perform(get("/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalMissions", greaterThanOrEqualTo(3)))
            .andExpect(jsonPath("$.successRate", greaterThanOrEqualTo(0.0)))
            .andExpect(jsonPath("$.successRate", lessThanOrEqualTo(100.0)))
            .andExpect(jsonPath("$.resourcesCollected", greaterThanOrEqualTo(0)));

        mockMvc.perform(get("/ranking"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name == 'E2E Pilot')]").exists());
    }

    @Test
    @DisplayName("E2E: actuator health endpoint retorna UP")
    void actuatorHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("E2E: requisicao invalida retorna 400 com mensagem padronizada")
    void validationError() throws Exception {
        mockMvc.perform(post("/astronauts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"\", \"rank\": \"\", \"experience\": -5 }"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message", containsString("name")));
    }

    private long createAstronaut(String name, String rank, int experience) throws Exception {
        String body = "{ \"name\": \"" + name + "\", \"rank\": \"" + rank + "\", \"experience\": " + experience + " }";
        return extractId(mockMvc.perform(post("/astronauts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isCreated())
            .andReturn());
    }

    private long createSpacecraft(String name, int fuel, int capacity, String status) throws Exception {
        String body = "{ \"name\": \"" + name + "\", \"fuel\": " + fuel
            + ", \"capacity\": " + capacity + ", \"status\": \"" + status + "\" }";
        return extractId(mockMvc.perform(post("/spacecraft")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isCreated())
            .andReturn());
    }

    private long createPlanet(String name, int distance, int dangerLevel, String resourcesJson) throws Exception {
        String body = "{ \"name\": \"" + name + "\", \"distance\": " + distance
            + ", \"dangerLevel\": " + dangerLevel + ", \"resources\": " + resourcesJson + " }";
        return extractId(mockMvc.perform(post("/planet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isCreated())
            .andReturn());
    }

    private long createMission(long spacecraftId, long planetId, long... astronautIds) throws Exception {
        StringBuilder ids = new StringBuilder();
        for (long id : astronautIds) {
            ids.append(id).append(", ");
        }
        String astronauts = ids.substring(0, ids.length() - 2);
        String body = "{ \"spacecraftId\": " + spacecraftId + ", \"planetId\": " + planetId
            + ", \"astronauts\": [" + astronauts + "] }";
        return extractId(mockMvc.perform(post("/mission")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isCreated())
            .andReturn());
    }

    private long extractId(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}