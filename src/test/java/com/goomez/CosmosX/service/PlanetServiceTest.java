package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Planet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanetServiceTest {

    @TempDir
    Path tempDir;

    private PlanetService service;

    @BeforeEach
    void setUp() throws Exception {
        String json = """
            [
              { "id": 1, "name": "Zorion", "distance": 500, "dangerLevel": 3, "resources": ["Iron", "Water"] }
            ]
            """;
        Files.writeString(Path.of(tempDir.toString(), "planet.json"), json);
        service = new PlanetService(tempDir.toString());
    }

    @Test
    @DisplayName("listAll returns all planets from JSON file")
    void listAll_returnsPlanets() {
        List<Planet> result = service.listAll();

        assertEquals(1, result.size());
        assertEquals("Zorion", result.get(0).getName());
        assertEquals(List.of("Iron", "Water"), result.get(0).getResources());
    }

    @Test
    @DisplayName("listById returns the planet with matching id")
    void listById_returnsPlanet() {
        Planet result = service.listById(1);

        assertEquals("Zorion", result.getName());
        assertEquals(3, result.getDangerLevel());
    }

    @Test
    @DisplayName("listById throws ResourceNotFoundException for unknown id")
    void listById_unknownId_throws() {
        assertThrows(ResourceNotFoundException.class, () -> service.listById(999));
    }

    @Test
    @DisplayName("add persists planet with auto-generated id")
    void add_persistsWithNextId() {
        Planet newPlanet = new Planet(0, "Mars-X", 500, 4, List.of("Gold"));
        Planet saved = service.add(newPlanet);

        assertEquals(2, saved.getId());
        assertEquals(2, service.listAll().size());
        assertEquals("Mars-X", service.listById(2).getName());
    }

    @Test
    @DisplayName("update modifies existing planet fields")
    void update_changesPlanet() {
        Planet updated = new Planet(1, "Zorion", 550, 5, List.of("Gold", "Water"));
        Planet result = service.update(1, updated);

        assertEquals(550, result.getDistance());
        assertEquals(5, result.getDangerLevel());
        assertEquals(List.of("Gold", "Water"), service.listById(1).getResources());
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException for unknown id")
    void update_unknownId_throws() {
        Planet updated = new Planet(999, "X", 1, 1, List.of());
        assertThrows(ResourceNotFoundException.class, () -> service.update(999, updated));
    }

    @Test
    @DisplayName("delete removes planet and returns true")
    void delete_removesPlanet() {
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