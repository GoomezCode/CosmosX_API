package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Mission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@Service
public class MissionService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo;

    public MissionService(@Value("${app.data.path:src/main/resources/data}") String dataPath) {
        this.arquivo = Paths.get(dataPath, "mission.json").toFile();
    }

    public List<Mission> listAll() {
        try {
            return mapper.readValue(arquivo, new TypeReference<List<Mission>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading mission data", e);
        }
    }

    public Mission listById(long id) {
        return listAll().stream()
            .filter(m -> m.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Mission not found with id: " + id));
    }

    public Mission add(Mission newMission) {
        try {
            List<Mission> missions = listAll();
            long nextId = missions.stream().mapToLong(Mission::getId).max().orElse(0) + 1;
            newMission.setId(nextId);
            missions.add(newMission);
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, missions);
            return newMission;
        } catch (Exception e) {
            throw new RuntimeException("Error saving mission", e);
        }
    }

    public Mission update(long id, Mission updatedMission) {
        try {
            List<Mission> missions = listAll();
            Mission existing = missions.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with id: " + id));
            existing.setPlanetId(updatedMission.getPlanetId());
            existing.setAstronauts(updatedMission.getAstronauts());
            existing.setStatus(updatedMission.getStatus());
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, missions);
            return existing;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating mission", e);
        }
    }

    public boolean delete(Long id) {
        try {
            List<Mission> missions = listAll();
            boolean removed = missions.removeIf(m -> m.getId() == id);
            if (removed) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, missions);
            }
            return removed;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting mission", e);
        }
    }
}