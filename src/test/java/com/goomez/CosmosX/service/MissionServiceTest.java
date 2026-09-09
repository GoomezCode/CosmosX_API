package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.MissionExecutionResponse;
import com.goomez.CosmosX.dto.ResourceFoundResponse;
import com.goomez.CosmosX.exception.InsufficientFuelException;
import com.goomez.CosmosX.exception.InvalidMissionStateException;
import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.model.Mission;
import com.goomez.CosmosX.model.MissionEvent;
import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.model.Spacecraft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MissionServiceTest {

    @TempDir
    Path tempDir;

    private MissionService service;
    private SpacecraftService spacecraftService;
    private PlanetService planetService;
    private AstronautService astronautService;
    private DangerService dangerService;
    private ResourceService resourceService;

    @BeforeEach
    void setUp() throws Exception {
        String json = """
            [
              { "id": 1, "spacecraftId": 1, "planetId": 1, "astronauts": [1, 2], "status": "PENDING" }
            ]
            """;
        Files.writeString(Path.of(tempDir.toString(), "mission.json"), json);

        spacecraftService = mock(SpacecraftService.class);
        planetService = mock(PlanetService.class);
        astronautService = mock(AstronautService.class);
        dangerService = mock(DangerService.class);
        resourceService = mock(ResourceService.class);

        service = new MissionService(tempDir.toString(),
            new FuelService(),
            dangerService,
            resourceService,
            spacecraftService,
            planetService,
            astronautService);
    }

    @Test
    @DisplayName("listAll returns all missions from JSON file")
    void listAll_returnsMissions() {
        List<Mission> result = service.listAll();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getSpacecraftId());
        assertEquals(1, result.get(0).getPlanetId());
        assertEquals(List.of(1L, 2L), result.get(0).getAstronauts());
    }

    @Test
    @DisplayName("listById returns the mission with matching id")
    void listById_returnsMission() {
        Mission result = service.listById(1);

        assertEquals("PENDING", result.getStatus());
    }

    @Test
    @DisplayName("listById throws ResourceNotFoundException for unknown id")
    void listById_unknownId_throws() {
        assertThrows(ResourceNotFoundException.class, () -> service.listById(999));
    }

    @Test
    @DisplayName("add persists mission with auto-generated id")
    void add_persistsWithNextId() {
        Mission newMission = new Mission(0, 1, 2, List.of(1L), "PENDING");
        Mission saved = service.add(newMission);

        assertEquals(2, saved.getId());
        assertEquals(2, service.listAll().size());
        assertEquals(2, service.listById(2).getPlanetId());
    }

    @Test
    @DisplayName("update modifies existing mission fields")
    void update_changesMission() {
        Mission updated = new Mission(1, 1, 2, List.of(1L, 3L), "IN_PROGRESS");
        Mission result = service.update(1, updated);

        assertEquals(2, result.getPlanetId());
        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals("IN_PROGRESS", service.listById(1).getStatus());
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException for unknown id")
    void update_unknownId_throws() {
        Mission updated = new Mission(999, 1, 1, List.of(1L), "PENDING");
        assertThrows(ResourceNotFoundException.class, () -> service.update(999, updated));
    }

    @Test
    @DisplayName("delete removes mission and returns true")
    void delete_removesMission() {
        boolean removed = service.delete(1L);

        assertTrue(removed);
        assertEquals(0, service.listAll().size());
    }

    @Test
    @DisplayName("delete for unknown id returns false")
    void delete_unknownId_returnsFalse() {
        boolean removed = service.delete(999L);

        assertFalse(removed);
        assertEquals(1, service.listAll().size());
    }

    @Test
    @DisplayName("executeMission completes successfully and generates resources")
    void executeMission_success() {
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 1000, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 1, List.of("Iron", "Water"));
        when(spacecraftService.listById(1)).thenReturn(spacecraft);
        when(planetService.listById(1)).thenReturn(planet);
        when(dangerService.resolveEvent(planet)).thenReturn(MissionEvent.SUCCESS);
        when(resourceService.generate(planet)).thenReturn(List.of(new ResourceFoundResponse("Iron", 25)));

        MissionExecutionResponse response = service.executeMission(1);

        Mission persisted = service.listById(1);
        assertEquals("SUCCESS", response.status());
        assertEquals(500, response.fuelConsumed());
        assertEquals(1, response.resourcesFound().size());
        assertEquals("Iron", response.resourcesFound().get(0).resource());
        assertEquals(List.of("Mission completed successfully", "Resources collected: Iron (25)"), response.events());
        assertEquals("SUCCESS", persisted.getStatus());
        assertEquals(500, persisted.getFuelConsumed());
        assertEquals("Iron", persisted.getResourcesFound().get(0).getResource());
        assertNotNull(persisted.getCompletedAt());
        verify(spacecraftService).update(1, spacecraft);
        assertEquals(500, spacecraft.getFuel());
    }

    @Test
    @DisplayName("executeMission throws InsufficientFuelException when fuel is below distance")
    void executeMission_insufficientFuel_throws() {
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 400, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 1, List.of("Iron"));
        when(spacecraftService.listById(1)).thenReturn(spacecraft);
        when(planetService.listById(1)).thenReturn(planet);

        assertThrows(InsufficientFuelException.class, () -> service.executeMission(1));
        assertEquals("PENDING", service.listById(1).getStatus());
    }

    @Test
    @DisplayName("executeMission throws InvalidMissionStateException when mission is not PENDING")
    void executeMission_notPending_throws() {
        service.update(1, new Mission(1, 1, 1, List.of(1L), "SUCCESS"));

        assertThrows(InvalidMissionStateException.class, () -> service.executeMission(1));
    }

    @Test
    @DisplayName("executeMission marks mission FAILED and damages spacecraft on mechanical failure")
    void executeMission_mechanicalFailure() {
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 1000, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 1, List.of("Iron"));
        when(spacecraftService.listById(1)).thenReturn(spacecraft);
        when(planetService.listById(1)).thenReturn(planet);
        when(dangerService.resolveEvent(planet)).thenReturn(MissionEvent.MECHANICAL_FAILURE);

        MissionExecutionResponse response = service.executeMission(1);

        assertEquals("FAILED", response.status());
        assertEquals("DAMAGED", spacecraft.getStatus());
        assertEquals(List.of("Mechanical failure detected", "Spacecraft damaged"), response.events());
        assertTrue(response.resourcesFound().isEmpty());
        verify(spacecraftService).update(1, spacecraft);
    }

    @Test
    @DisplayName("executeMission reduces astronaut XP on alien attack")
    void executeMission_alienAttack() {
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 1000, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 1, List.of("Iron"));
        Astronaut daniel = new Astronaut(1, "Daniel", "Commander", 1200);
        Astronaut laura = new Astronaut(2, "Laura", "Pilot", 800);
        when(spacecraftService.listById(1)).thenReturn(spacecraft);
        when(planetService.listById(1)).thenReturn(planet);
        when(astronautService.listById(1)).thenReturn(daniel);
        when(astronautService.listById(2)).thenReturn(laura);
        when(dangerService.resolveEvent(planet)).thenReturn(MissionEvent.ALIEN_ATTACK);

        MissionExecutionResponse response = service.executeMission(1);

        assertEquals("FAILED", response.status());
        assertEquals(1100, daniel.getExperience());
        assertEquals(700, laura.getExperience());
        verify(astronautService).update(1, daniel);
        verify(astronautService).update(2, laura);
        assertEquals(List.of("Alien attack repelled", "Astronauts lost XP"), response.events());
    }

    @Test
    @DisplayName("executeMission marks mission FAILED and loses resources on cosmic storm")
    void executeMission_cosmicStorm() {
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 1000, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 1, List.of("Iron"));
        when(spacecraftService.listById(1)).thenReturn(spacecraft);
        when(planetService.listById(1)).thenReturn(planet);
        when(dangerService.resolveEvent(planet)).thenReturn(MissionEvent.COSMIC_STORM);

        MissionExecutionResponse response = service.executeMission(1);

        assertEquals("FAILED", response.status());
        assertEquals(List.of("Cosmic storm intercepted", "Resources lost"), response.events());
        assertEquals("FAILED", service.listById(1).getStatus());
    }

    @Test
    @DisplayName("listHistory returns all missions with resolved names")
    void listHistory_returnsAll() throws Exception {
        writeMissions(historyJson());
        stubHistoryDependencies();

        var history = service.listHistory(null, null);

        assertEquals(2, history.size());
        assertEquals("Zorion", history.get(0).planetName());
        assertEquals(List.of("Daniel", "Laura"), history.get(0).astronauts());
        assertEquals(500, history.get(0).fuelConsumed());
        assertEquals(List.of("Iron", "Water"), history.get(0).resourcesFound());
        assertEquals("2026-09-09T14:30:00", history.get(0).completedAt());
    }

    @Test
    @DisplayName("listHistory filters by status")
    void listHistory_filtersByStatus() throws Exception {
        writeMissions(historyJson());
        stubHistoryDependencies();

        var history = service.listHistory("FAILED", null);

        assertEquals(1, history.size());
        assertEquals("FAILED", history.get(0).status());
    }

    @Test
    @DisplayName("listHistory filters by planetId")
    void listHistory_filtersByPlanetId() throws Exception {
        writeMissions(historyJson());
        Planet planet2 = new Planet(2, "Mars-X", 600, 4, List.of("Gold"));
        when(planetService.listById(2)).thenReturn(planet2);
        when(astronautService.listById(1)).thenReturn(new Astronaut(1, "Daniel", "Commander", 1200));

        var history = service.listHistory(null, 2L);

        assertEquals(1, history.size());
        assertEquals("Mars-X", history.get(0).planetName());
    }

    private void stubHistoryDependencies() {
        Planet planet1 = new Planet(1, "Zorion", 500, 1, List.of("Iron", "Water"));
        Planet planet2 = new Planet(2, "Mars-X", 600, 4, List.of("Gold"));
        when(planetService.listById(1)).thenReturn(planet1);
        when(planetService.listById(2)).thenReturn(planet2);
        when(astronautService.listById(1)).thenReturn(new Astronaut(1, "Daniel", "Commander", 1200));
        when(astronautService.listById(2)).thenReturn(new Astronaut(2, "Laura", "Pilot", 800));
    }

    private void writeMissions(String json) throws Exception {
        Files.writeString(Path.of(tempDir.toString(), "mission.json"), json);
    }

    private String historyJson() {
        return """
            [
              { "id": 1, "spacecraftId": 1, "planetId": 1, "astronauts": [1, 2], "status": "SUCCESS",
                "fuelConsumed": 500,
                "resourcesFound": [
                  { "resource": "Iron", "quantity": 25 },
                  { "resource": "Water", "quantity": 50 }
                ],
                "completedAt": "2026-09-09T14:30:00" },
              { "id": 2, "spacecraftId": 1, "planetId": 2, "astronauts": [1], "status": "FAILED",
                "fuelConsumed": 600,
                "resourcesFound": [],
                "completedAt": "2026-09-09T15:00:00" }
            ]
            """;
    }
}