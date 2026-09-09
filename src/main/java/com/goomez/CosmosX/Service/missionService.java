package com.goomez.CosmosX.service;

import com.goomez.CosmosX.model.Mission;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Service
public class MissionService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo = new File("src/main/resources/data/mission.json");

    public List<Mission> listAll() {
        try {
            return mapper.readValue(arquivo, new TypeReference<List<Mission>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading mission data", e);
        }
    }

    public Mission add(Mission newMission) {
        try {
            List<Mission> missions = listAll();
            missions.add(newMission);
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, missions);
            return newMission;
        } catch (Exception e) {
            throw new RuntimeException("Error saving mission", e);
        }
    }
}
