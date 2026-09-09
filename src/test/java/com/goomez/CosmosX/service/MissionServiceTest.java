package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Mission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MissionServiceTest {

    @TempDir
    Path tempDir;

    private MissionService service;

    @BeforeEach
    void setUp() throws Exception {
        String json = """
            [
              { "id": 1, "planetId": 1, "astronauts": [1, 2], "status": "PENDING" }
            ]
            """;
        Files.writeString(Path.of(tempDir.toString(), "mission.json"), json);
        service = new MissionService(tempDir.toString());
    }

    @Test
    @DisplayName("listAll returns all missions from JSON file")
    void listAll_returnsMissions() {
        List<Mission> result = service.listAll();

        assertEquals(1, result.size());
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
        Mission newMission = new Mission(0, 2, List.of(1L), "PENDING");
        Mission saved = service.add(newMission);

        assertEquals(2, saved.getId());
        assertEquals(2, service.listAll().size());
        assertEquals(2, service.listById(2).getPlanetId());
    }

    @Test
    @DisplayName("update modifies existing mission fields")
    void update_changesMission() {
        Mission updated = new Mission(1, 2, List.of(1L, 3L), "IN_PROGRESS");
        Mission result = service.update(1, updated);

        assertEquals(2, result.getPlanetId());
        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals("IN_PROGRESS", service.listById(1).getStatus());
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException for unknown id")
    void update_unknownId_throws() {
        Mission updated = new Mission(999, 1, List.of(1L), "PENDING");
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
}