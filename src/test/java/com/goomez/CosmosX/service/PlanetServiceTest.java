package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.repository.PlanetRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetServiceTest {

    @Mock
    private PlanetRepository repository;

    private PlanetService service;

    @BeforeEach
    void setUp() {
        service = new PlanetService(repository);
    }

    @Test
    @DisplayName("listAll returns all planets from repository")
    void listAll_returnsPlanets() {
        when(repository.findAll()).thenReturn(List.of(
            new Planet(1, "Zorion", 500, 3, List.of("Iron", "Water"))
        ));

        List<Planet> result = service.listAll();

        assertEquals(1, result.size());
        assertEquals("Zorion", result.get(0).getName());
        assertEquals(500, result.get(0).getDistance());
        assertEquals(3, result.get(0).getDangerLevel());
    }

    @Test
    @DisplayName("listById returns the planet with matching id")
    void listById_returnsPlanet() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Planet(1, "Zorion", 500, 3, List.of("Iron", "Water"))));

        Planet result = service.listById(1);

        assertEquals("Zorion", result.getName());
        assertEquals(3, result.getDangerLevel());
    }

    @Test
    @DisplayName("listById throws ResourceNotFoundException for unknown id")
    void listById_unknownId_throws() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.listById(999));
    }

    @Test
    @DisplayName("add persists planet with auto-generated id")
    void add_persistsWithNextId() {
        when(repository.save(any(Planet.class))).thenAnswer(inv -> {
            Planet p = inv.getArgument(0);
            p.setId(2);
            return p;
        });

        Planet saved = service.add(new Planet(0, "Nebulon-7", 1200, 6, List.of("Gold")));

        assertEquals(2, saved.getId());
        verify(repository).save(any(Planet.class));
    }

    @Test
    @DisplayName("update modifies existing planet fields")
    void update_changesPlanet() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Planet(1, "Zorion", 500, 3, List.of("Iron"))));
        when(repository.save(any(Planet.class))).thenAnswer(inv -> inv.getArgument(0));

        Planet result = service.update(1, new Planet(1, "Zorion", 600, 5, List.of("Gold", "Water")));

        assertEquals(600, result.getDistance());
        assertEquals(5, result.getDangerLevel());
        assertEquals(List.of("Gold", "Water"), result.getResources());
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException for unknown id")
    void update_unknownId_throws() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(999, new Planet(999, "X", 1, 1, List.of())));
    }

    @Test
    @DisplayName("delete removes planet and returns true")
    void delete_removesPlanet() {
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
}