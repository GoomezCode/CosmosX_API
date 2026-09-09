package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Astronaut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AstronautServiceTest {

    @TempDir
    Path tempDir;

    private AstronautService service;

    @BeforeEach
    void setUp() throws Exception {
        String json = """
            [
              { "id": 1, "name": "Daniel", "rank": "Commander", "experience": 1200 }
            ]
            """;
        Files.writeString(Path.of(tempDir.toString(), "astronaut.json"), json);
        service = new AstronautService(tempDir.toString());
    }

    @Test
    @DisplayName("listAll returns all astronauts from JSON file")
    void listAll_returnsAstronauts() {
        List<Astronaut> result = service.listAll();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Daniel", result.get(0).getName());
        assertEquals("Commander", result.get(0).getRank());
        assertEquals(1200, result.get(0).getExperience());
    }

    @Test
    @DisplayName("listById returns the astronaut with matching id")
    void listById_returnsAstronaut() {
        Astronaut result = service.listById(1);

        assertEquals("Daniel", result.getName());
    }

    @Test
    @DisplayName("listById throws ResourceNotFoundException for unknown id")
    void listById_unknownId_throws() {
        assertThrows(ResourceNotFoundException.class, () -> service.listById(999));
    }

    @Test
    @DisplayName("add persists astronaut with auto-generated id")
    void add_persistsWithNextId() {
        Astronaut newAstronaut = new Astronaut(0, "Laura", "Pilot", 800);
        Astronaut saved = service.add(newAstronaut);

        assertEquals(2, saved.getId());
        assertEquals(2, service.listAll().size());
        assertEquals("Laura", service.listById(2).getName());
    }

    @Test
    @DisplayName("update modifies existing astronaut fields")
    void update_changesAstronaut() {
        Astronaut updated = new Astronaut(1, "Daniel", "Captain", 1500);
        Astronaut result = service.update(1, updated);

        assertEquals("Captain", result.getRank());
        assertEquals(1500, result.getExperience());
        assertEquals("Captain", service.listById(1).getRank());
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException for unknown id")
    void update_unknownId_throws() {
        Astronaut updated = new Astronaut(999, "X", "Y", 1);
        assertThrows(ResourceNotFoundException.class, () -> service.update(999, updated));
    }

    @Test
    @DisplayName("delete removes astronaut and returns true")
    void delete_removesAstronaut() {
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