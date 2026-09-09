package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Spacecraft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpacecraftServiceTest {

    @TempDir
    Path tempDir;

    private SpacecraftService service;

    @BeforeEach
    void setUp() throws Exception {
        String json = """
            [
              { "id": 1, "name": "Falcon-X", "fuel": 1000, "capacity": 5, "status": "READY" }
            ]
            """;
        Files.writeString(Path.of(tempDir.toString(), "spacecraft.json"), json);
        service = new SpacecraftService(tempDir.toString());
    }

    @Test
    @DisplayName("listAll returns all spacecraft from JSON file")
    void listAll_returnsSpacecraft() {
        List<Spacecraft> result = service.listAll();

        assertEquals(1, result.size());
        assertEquals("Falcon-X", result.get(0).getName());
        assertEquals(1000, result.get(0).getFuel());
    }

    @Test
    @DisplayName("listById returns the spacecraft with matching id")
    void listById_returnsSpacecraft() {
        Spacecraft result = service.listById(1);

        assertEquals("Falcon-X", result.getName());
        assertEquals("READY", result.getStatus());
    }

    @Test
    @DisplayName("listById throws ResourceNotFoundException for unknown id")
    void listById_unknownId_throws() {
        assertThrows(ResourceNotFoundException.class, () -> service.listById(999));
    }

    @Test
    @DisplayName("add persists spacecraft with auto-generated id")
    void add_persistsWithNextId() {
        Spacecraft newSpacecraft = new Spacecraft(0, "Apollo-7", 800, 3, "READY");
        Spacecraft saved = service.add(newSpacecraft);

        assertEquals(2, saved.getId());
        assertEquals(2, service.listAll().size());
        assertEquals("Apollo-7", service.listById(2).getName());
    }

    @Test
    @DisplayName("update modifies existing spacecraft fields")
    void update_changesSpacecraft() {
        Spacecraft updated = new Spacecraft(1, "Falcon-X", 500, 5, "MAINTENANCE");
        Spacecraft result = service.update(1, updated);

        assertEquals(500, result.getFuel());
        assertEquals("MAINTENANCE", result.getStatus());
        assertEquals("MAINTENANCE", service.listById(1).getStatus());
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException for unknown id")
    void update_unknownId_throws() {
        Spacecraft updated = new Spacecraft(999, "X", 1, 1, "READY");
        assertThrows(ResourceNotFoundException.class, () -> service.update(999, updated));
    }

    @Test
    @DisplayName("delete removes spacecraft and returns true")
    void delete_removesSpacecraft() {
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