package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Spacecraft;
import com.goomez.CosmosX.repository.SpacecraftRepository;
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
class SpacecraftServiceTest {

    @Mock
    private SpacecraftRepository repository;

    private SpacecraftService service;

    @BeforeEach
    void setUp() {
        service = new SpacecraftService(repository);
    }

    @Test
    @DisplayName("listAll returns all spacecraft from repository")
    void listAll_returnsSpacecraft() {
        when(repository.findAll()).thenReturn(List.of(
            new Spacecraft(1, "Falcon-X", 1000, 5, "READY")
        ));

        List<Spacecraft> result = service.listAll();

        assertEquals(1, result.size());
        assertEquals("Falcon-X", result.get(0).getName());
        assertEquals(1000, result.get(0).getFuel());
    }

    @Test
    @DisplayName("listById returns the spacecraft with matching id")
    void listById_returnsSpacecraft() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Spacecraft(1, "Falcon-X", 1000, 5, "READY")));

        Spacecraft result = service.listById(1);

        assertEquals("Falcon-X", result.getName());
        assertEquals("READY", result.getStatus());
    }

    @Test
    @DisplayName("listById throws ResourceNotFoundException for unknown id")
    void listById_unknownId_throws() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.listById(999));
    }

    @Test
    @DisplayName("add persists spacecraft with auto-generated id")
    void add_persistsWithNextId() {
        when(repository.save(any(Spacecraft.class))).thenAnswer(inv -> {
            Spacecraft s = inv.getArgument(0);
            s.setId(2);
            return s;
        });

        Spacecraft saved = service.add(new Spacecraft(0, "Apollo-7", 800, 3, "READY"));

        assertEquals(2, saved.getId());
        verify(repository).save(any(Spacecraft.class));
    }

    @Test
    @DisplayName("update modifies existing spacecraft fields")
    void update_changesSpacecraft() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Spacecraft(1, "Falcon-X", 1000, 5, "READY")));
        when(repository.save(any(Spacecraft.class))).thenAnswer(inv -> inv.getArgument(0));

        Spacecraft result = service.update(1, new Spacecraft(1, "Falcon-X", 500, 5, "MAINTENANCE"));

        assertEquals(500, result.getFuel());
        assertEquals("MAINTENANCE", result.getStatus());
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException for unknown id")
    void update_unknownId_throws() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(999, new Spacecraft(999, "X", 1, 1, "READY")));
    }

    @Test
    @DisplayName("delete removes spacecraft and returns true")
    void delete_removesSpacecraft() {
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