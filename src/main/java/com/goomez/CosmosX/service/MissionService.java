package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.MissionExecutionResponse;
import com.goomez.CosmosX.dto.ResourceFoundResponse;
import com.goomez.CosmosX.exception.InvalidMissionStateException;
import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.model.Mission;
import com.goomez.CosmosX.model.MissionEvent;
import com.goomez.CosmosX.model.MissionStatus;
import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.model.Spacecraft;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MissionService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo;
    private final FuelService fuelService;
    private final DangerService dangerService;
    private final ResourceService resourceService;
    private final SpacecraftService spacecraftService;
    private final PlanetService planetService;
    private final AstronautService astronautService;

    public MissionService(@Value("${app.data.path:src/main/resources/data}") String dataPath,
                          FuelService fuelService,
                          DangerService dangerService,
                          ResourceService resourceService,
                          SpacecraftService spacecraftService,
                          PlanetService planetService,
                          AstronautService astronautService) {
        this.arquivo = Paths.get(dataPath, "mission.json").toFile();
        this.fuelService = fuelService;
        this.dangerService = dangerService;
        this.resourceService = resourceService;
        this.spacecraftService = spacecraftService;
        this.planetService = planetService;
        this.astronautService = astronautService;
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
            existing.setSpacecraftId(updatedMission.getSpacecraftId());
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

    public MissionExecutionResponse executeMission(long id) {
        Mission mission = listById(id);
        if (!MissionStatus.PENDING.name().equals(mission.getStatus())) {
            throw new InvalidMissionStateException("Mission must be PENDING to start, but was " + mission.getStatus());
        }

        Spacecraft spacecraft = spacecraftService.listById(mission.getSpacecraftId());
        Planet planet = planetService.listById(mission.getPlanetId());

        int fuelConsumed = fuelService.calculateConsumption(spacecraft, planet);

        mission.setStatus(MissionStatus.IN_PROGRESS.name());
        save(mission);

        MissionEvent event = dangerService.resolveEvent(planet);
        List<String> events = new ArrayList<>();
        List<ResourceFoundResponse> resourcesFound = List.of();

        switch (event) {
            case SUCCESS -> {
                mission.setStatus(MissionStatus.SUCCESS.name());
                resourcesFound = resourceService.generate(planet);
                events.add("Mission completed successfully");
                resourcesFound.forEach(r -> events.add("Resources collected: " + r.resource() + " (" + r.quantity() + ")"));
            }
            case MECHANICAL_FAILURE -> {
                mission.setStatus(MissionStatus.FAILED.name());
                spacecraft.setStatus("DAMAGED");
                events.add("Mechanical failure detected");
                events.add("Spacecraft damaged");
            }
            case ALIEN_ATTACK -> {
                mission.setStatus(MissionStatus.FAILED.name());
                for (Long astronautId : mission.getAstronauts()) {
                    Astronaut astronaut = astronautService.listById(astronautId);
                    astronaut.setExperience(Math.max(0, astronaut.getExperience() - 100));
                    astronautService.update(astronaut.getId(), astronaut);
                }
                events.add("Alien attack repelled");
                events.add("Astronauts lost XP");
            }
            case COSMIC_STORM -> {
                mission.setStatus(MissionStatus.FAILED.name());
                events.add("Cosmic storm intercepted");
                events.add("Resources lost");
            }
        }

        spacecraft.setFuel(spacecraft.getFuel() - fuelConsumed);
        spacecraftService.update(spacecraft.getId(), spacecraft);

        save(mission);

        return new MissionExecutionResponse(mission.getId(), mission.getStatus(), fuelConsumed, resourcesFound, events);
    }

    private void save(Mission mission) {
        try {
            List<Mission> missions = listAll();
            for (int i = 0; i < missions.size(); i++) {
                if (missions.get(i).getId() == mission.getId()) {
                    missions.set(i, mission);
                    break;
                }
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, missions);
        } catch (Exception e) {
            throw new RuntimeException("Error saving mission", e);
        }
    }
}