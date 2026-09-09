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
import com.goomez.CosmosX.model.ResourceFound;
import com.goomez.CosmosX.model.Spacecraft;
import com.goomez.CosmosX.repository.MissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository repository;
    @Mock
    private SpacecraftService spacecraftService;
    @Mock
    private PlanetService planetService;
    @Mock
    private AstronautService astronautService;
    @Mock
    private DangerService dangerService;
    @Mock
    private ResourceService resourceService;

    private MissionService service;

    @BeforeEach
    void setUp() {
        service = new MissionService(repository,
            new FuelService(),
            dangerService,
            resourceService,
            spacecraftService,
            planetService,
            astronautService);
    }

    @Test
    @DisplayName("listAll returns all missions from repository")
    void listAll_returnsMissions() {
        when(repository.findAll()).thenReturn(List.of(new Mission(1, 1, 1, List.of(1L, 2L), "PENDING")));

        List<Mission> result = service.listAll();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getSpacecraftId());
        assertEquals(1, result.get(0).getPlanetId());
        assertEquals(List.of(1L, 2L), result.get(0).getAstronauts());
    }

    @Test
    @DisplayName("listById returns the mission with matching id")
    void listById_returnsMission() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Mission(1, 1, 1, List.of(1L), "PENDING")));

        Mission result = service.listById(1);

        assertEquals("PENDING", result.getStatus());
    }

    @Test
    @DisplayName("listById throws ResourceNotFoundException for unknown id")
    void listById_unknownId_throws() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.listById(999));
    }

    @Test
    @DisplayName("add persists mission with auto-generated id")
    void add_persistsWithNextId() {
        when(repository.save(any(Mission.class))).thenAnswer(inv -> {
            Mission m = inv.getArgument(0);
            m.setId(2);
            return m;
        });

        Mission saved = service.add(new Mission(0, 1, 2, List.of(1L), "PENDING"));

        assertEquals(2, saved.getId());
        verify(repository).save(any(Mission.class));
    }

    @Test
    @DisplayName("update modifies existing mission fields")
    void update_changesMission() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Mission(1, 1, 1, List.of(1L), "PENDING")));
        when(repository.save(any(Mission.class))).thenAnswer(inv -> inv.getArgument(0));

        Mission result = service.update(1, new Mission(1, 1, 2, List.of(1L, 3L), "IN_PROGRESS"));

        assertEquals(2, result.getPlanetId());
        assertEquals("IN_PROGRESS", result.getStatus());
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException for unknown id")
    void update_unknownId_throws() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(999, new Mission(999, 1, 1, List.of(1L), "PENDING")));
    }

    @Test
    @DisplayName("delete removes mission and returns true")
    void delete_removesMission() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean removed = service.delete(1L);

        assertTrue(removed);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete for unknown id returns false and does not delete")
    void delete_unknownId_returnsFalse() {
        when(repository.existsById(999L)).thenReturn(false);

        boolean removed = service.delete(999L);

        assertFalse(removed);
        verify(repository, never()).deleteById(any(Long.class));
    }

    @Test
    @DisplayName("executeMission completes successfully and generates resources")
    void executeMission_success() {
        Mission mission = new Mission(1, 1, 1, List.of(1L, 2L), "PENDING");
        when(repository.findById(1L)).thenReturn(Optional.of(mission));
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 1000, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 1, List.of("Iron", "Water"));
        when(spacecraftService.listById(1)).thenReturn(spacecraft);
        when(planetService.listById(1)).thenReturn(planet);
        when(dangerService.resolveEvent(planet)).thenReturn(MissionEvent.SUCCESS);
        when(resourceService.generate(planet)).thenReturn(List.of(new ResourceFoundResponse("Iron", 25)));

        MissionExecutionResponse response = service.executeMission(1);

        assertEquals("SUCCESS", response.status());
        assertEquals(500, response.fuelConsumed());
        assertEquals(1, response.resourcesFound().size());
        assertEquals("Iron", response.resourcesFound().get(0).resource());
        assertEquals(List.of("Mission completed successfully", "Resources collected: Iron (25)"), response.events());
        assertEquals("SUCCESS", mission.getStatus());
        assertEquals(500, mission.getFuelConsumed());
        assertEquals("Iron", mission.getResourcesFound().get(0).getResource());
        assertNotNull(mission.getCompletedAt());
        verify(repository).save(mission);
        verify(spacecraftService).update(1, spacecraft);
        assertEquals(500, spacecraft.getFuel());
    }

    @Test
    @DisplayName("executeMission throws InsufficientFuelException when fuel is below distance")
    void executeMission_insufficientFuel_throws() {
        Mission mission = new Mission(1, 1, 1, List.of(1L, 2L), "PENDING");
        when(repository.findById(1L)).thenReturn(Optional.of(mission));
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 400, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 1, List.of("Iron"));
        when(spacecraftService.listById(1)).thenReturn(spacecraft);
        when(planetService.listById(1)).thenReturn(planet);

        assertThrows(InsufficientFuelException.class, () -> service.executeMission(1));
        assertEquals("PENDING", mission.getStatus());
    }

    @Test
    @DisplayName("executeMission throws InvalidMissionStateException when mission is not PENDING")
    void executeMission_notPending_throws() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Mission(1, 1, 1, List.of(1L), "SUCCESS")));

        assertThrows(InvalidMissionStateException.class, () -> service.executeMission(1));
    }

    @Test
    @DisplayName("executeMission marks mission FAILED and damages spacecraft on mechanical failure")
    void executeMission_mechanicalFailure() {
        Mission mission = new Mission(1, 1, 1, List.of(1L, 2L), "PENDING");
        when(repository.findById(1L)).thenReturn(Optional.of(mission));
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
        Mission mission = new Mission(1, 1, 1, List.of(1L, 2L), "PENDING");
        when(repository.findById(1L)).thenReturn(Optional.of(mission));
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
        Mission mission = new Mission(1, 1, 1, List.of(1L, 2L), "PENDING");
        when(repository.findById(1L)).thenReturn(Optional.of(mission));
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 1000, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 1, List.of("Iron"));
        when(spacecraftService.listById(1)).thenReturn(spacecraft);
        when(planetService.listById(1)).thenReturn(planet);
        when(dangerService.resolveEvent(planet)).thenReturn(MissionEvent.COSMIC_STORM);

        MissionExecutionResponse response = service.executeMission(1);

        assertEquals("FAILED", response.status());
        assertEquals(List.of("Cosmic storm intercepted", "Resources lost"), response.events());
        assertEquals("FAILED", mission.getStatus());
    }

    @Test
    @DisplayName("listHistory returns all missions with resolved names")
    void listHistory_returnsAll() {
        when(repository.findAll()).thenReturn(List.of(mission1(), mission2()));
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
    void listHistory_filtersByStatus() {
        when(repository.findAll()).thenReturn(List.of(mission1(), mission2()));
        stubHistoryDependencies();

        var history = service.listHistory("FAILED", null);

        assertEquals(1, history.size());
        assertEquals("FAILED", history.get(0).status());
    }

    @Test
    @DisplayName("listHistory filters by planetId")
    void listHistory_filtersByPlanetId() {
        when(repository.findAll()).thenReturn(List.of(mission1(), mission2()));
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
        lenient().when(planetService.listById(1)).thenReturn(planet1);
        lenient().when(planetService.listById(2)).thenReturn(planet2);
        lenient().when(astronautService.listById(1)).thenReturn(new Astronaut(1, "Daniel", "Commander", 1200));
        lenient().when(astronautService.listById(2)).thenReturn(new Astronaut(2, "Laura", "Pilot", 800));
    }

    private Mission mission1() {
        Mission m = new Mission(1, 1, 1, List.of(1L, 2L), "SUCCESS");
        m.setFuelConsumed(500);
        m.setResourcesFound(List.of(new ResourceFound("Iron", 25), new ResourceFound("Water", 50)));
        m.setCompletedAt("2026-09-09T14:30:00");
        return m;
    }

    private Mission mission2() {
        Mission m = new Mission(2, 1, 2, List.of(1L), "FAILED");
        m.setFuelConsumed(600);
        m.setResourcesFound(List.of());
        m.setCompletedAt("2026-09-09T15:00:00");
        return m;
    }
}