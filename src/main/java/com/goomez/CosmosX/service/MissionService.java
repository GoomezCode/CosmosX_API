package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.MissionExecutionResponse;
import com.goomez.CosmosX.dto.MissionHistoryResponse;
import com.goomez.CosmosX.dto.ResourceFoundResponse;
import com.goomez.CosmosX.exception.InvalidMissionStateException;
import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.model.Mission;
import com.goomez.CosmosX.model.MissionEvent;
import com.goomez.CosmosX.model.MissionStatus;
import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.model.ResourceFound;
import com.goomez.CosmosX.model.Spacecraft;
import com.goomez.CosmosX.repository.MissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MissionService {
    private final MissionRepository missionRepository;
    private final FuelService fuelService;
    private final DangerService dangerService;
    private final ResourceService resourceService;
    private final SpacecraftService spacecraftService;
    private final PlanetService planetService;
    private final AstronautService astronautService;

    public MissionService(MissionRepository missionRepository,
                          FuelService fuelService,
                          DangerService dangerService,
                          ResourceService resourceService,
                          SpacecraftService spacecraftService,
                          PlanetService planetService,
                          AstronautService astronautService) {
        this.missionRepository = missionRepository;
        this.fuelService = fuelService;
        this.dangerService = dangerService;
        this.resourceService = resourceService;
        this.spacecraftService = spacecraftService;
        this.planetService = planetService;
        this.astronautService = astronautService;
    }

    public List<Mission> listAll() {
        return missionRepository.findAll();
    }

    public Mission listById(long id) {
        return missionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mission not found with id: " + id));
    }

    @Transactional
    public Mission add(Mission newMission) {
        return missionRepository.save(newMission);
    }

    @Transactional
    public Mission update(long id, Mission updatedMission) {
        Mission existing = listById(id);
        existing.setSpacecraftId(updatedMission.getSpacecraftId());
        existing.setPlanetId(updatedMission.getPlanetId());
        existing.setAstronauts(updatedMission.getAstronauts());
        existing.setStatus(updatedMission.getStatus());
        return missionRepository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!missionRepository.existsById(id)) {
            return false;
        }
        missionRepository.deleteById(id);
        return true;
    }

    @Transactional
    public MissionExecutionResponse executeMission(long id) {
        Mission mission = listById(id);
        if (!MissionStatus.PENDING.name().equals(mission.getStatus())) {
            throw new InvalidMissionStateException("Mission must be PENDING to start, but was " + mission.getStatus());
        }

        Spacecraft spacecraft = spacecraftService.listById(mission.getSpacecraftId());
        Planet planet = planetService.listById(mission.getPlanetId());

        int fuelConsumed = fuelService.calculateConsumption(spacecraft, planet);

        MissionEvent event = dangerService.resolveEvent(planet);
        List<String> events = new ArrayList<>();
        List<ResourceFoundResponse> resourcesFound = List.of();

        switch (event) {
            case SUCCESS -> {
                mission.setStatus(MissionStatus.SUCCESS.name());
                resourcesFound = resourceService.generate(planet);
                mission.setResourcesFound(resourcesFound.stream()
                    .map(r -> new ResourceFound(r.resource(), r.quantity()))
                    .toList());
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

        mission.setFuelConsumed(fuelConsumed);
        mission.setCompletedAt(LocalDateTime.now().toString());

        missionRepository.save(mission);

        return new MissionExecutionResponse(mission.getId(), mission.getStatus(), fuelConsumed, resourcesFound, events);
    }

    public List<MissionHistoryResponse> listHistory(String status, Long planetId) {
        return listAll().stream()
            .filter(m -> status == null || status.isBlank() || m.getStatus().equalsIgnoreCase(status))
            .filter(m -> planetId == null || m.getPlanetId() == planetId)
            .map(this::toHistory)
            .toList();
    }

    private MissionHistoryResponse toHistory(Mission mission) {
        Planet planet = planetService.listById(mission.getPlanetId());
        List<String> astronautNames = mission.getAstronauts() == null ? List.of()
            : mission.getAstronauts().stream()
                .map(id -> astronautService.listById(id).getName())
                .toList();
        List<String> resourceNames = mission.getResourcesFound() == null ? List.of()
            : mission.getResourcesFound().stream()
                .map(ResourceFound::getResource)
                .toList();
        return new MissionHistoryResponse(
            mission.getId(),
            planet.getName(),
            astronautNames,
            mission.getStatus(),
            mission.getFuelConsumed(),
            resourceNames,
            mission.getCompletedAt()
        );
    }
}