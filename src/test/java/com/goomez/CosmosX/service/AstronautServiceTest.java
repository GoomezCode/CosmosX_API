package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.repository.AstronautRepository;
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
class AstronautServiceTest {

    @Mock
    private AstronautRepository repository;

    private AstronautService service;

    @BeforeEach
    void setUp() {
        service = new AstronautService(repository);
    }

    @Test
    @DisplayName("listAll returns all astronauts from repository")
    void listAll_returnsAstronauts() {
        when(repository.findAll()).thenReturn(List.of(
            new Astronaut(1, "Daniel", "Commander", 1200)
        ));

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
        when(repository.findById(1L)).thenReturn(Optional.of(new Astronaut(1, "Daniel", "Commander", 1200)));

        Astronaut result = service.listById(1);

        assertEquals("Daniel", result.getName());
    }

    @Test
    @DisplayName("listById throws ResourceNotFoundException for unknown id")
    void listById_unknownId_throws() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.listById(999));
    }

    @Test
    @DisplayName("add persists astronaut with auto-generated id")
    void add_persistsWithNextId() {
        when(repository.save(any(Astronaut.class))).thenAnswer(inv -> {
            Astronaut a = inv.getArgument(0);
            a.setId(2);
            return a;
        });

        Astronaut saved = service.add(new Astronaut(0, "Laura", "Pilot", 800));

        assertEquals(2, saved.getId());
        verify(repository).save(any(Astronaut.class));
    }

    @Test
    @DisplayName("update modifies existing astronaut fields")
    void update_changesAstronaut() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Astronaut(1, "Daniel", "Commander", 1200)));
        when(repository.save(any(Astronaut.class))).thenAnswer(inv -> inv.getArgument(0));

        Astronaut result = service.update(1, new Astronaut(1, "Daniel", "Captain", 1500));

        assertEquals("Captain", result.getRank());
        assertEquals(1500, result.getExperience());
    }

    @Test
    @DisplayName("update throws ResourceNotFoundException for unknown id")
    void update_unknownId_throws() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(999, new Astronaut(999, "X", "Y", 1)));
    }

    @Test
    @DisplayName("delete removes astronaut and returns true")
    void delete_removesAstronaut() {
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