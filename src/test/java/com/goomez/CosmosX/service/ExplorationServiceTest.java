package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.PlanetDiscoveryRequest;
import com.goomez.CosmosX.dto.PlanetDiscoveryResponse;
import com.goomez.CosmosX.model.Planet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExplorationServiceTest {

    private final PlanetService planetService = mock(PlanetService.class);

    private static final List<String> BASIC = List.of("Iron", "Water", "Stone");
    private static final List<String> FULL_POOL = List.of("Iron", "Water", "Stone", "Gold", "Titanium", "Silicon", "Crystal", "Platinum", "Uranium");

    @Test
    @DisplayName("discover persists planet with provided fields and discoveredAt")
    void discover_persistsPlanet() {
        when(planetService.add(any(Planet.class))).thenAnswer(inv -> {
            Planet p = inv.getArgument(0);
            p.setId(5);
            return p;
        });
        ExplorationService service = new ExplorationService(planetService, new Random(42));

        PlanetDiscoveryResponse response = service.discover(new PlanetDiscoveryRequest("Nebulon-7", 1200, 6));

        assertEquals(5, response.id());
        assertEquals("Nebulon-7", response.name());
        assertEquals(1200, response.distance());
        assertEquals(6, response.dangerLevel());
        assertFalse(response.resources().isEmpty());
        assertTrue(FULL_POOL.containsAll(response.resources()));
        assertNotNull(response.discoveredAt());
        verify(planetService).add(any(Planet.class));
    }

    @Test
    @DisplayName("discover on safe planet only generates basic resources")
    void discover_safePlanet_basicResources() {
        when(planetService.add(any(Planet.class))).thenAnswer(inv -> inv.getArgument(0));
        ExplorationService service = new ExplorationService(planetService, new Random(7));

        PlanetDiscoveryResponse response = service.discover(new PlanetDiscoveryRequest("Aurora", 100, 0));

        assertFalse(response.resources().isEmpty());
        assertTrue(BASIC.containsAll(response.resources()));
    }

    @Test
    @DisplayName("discover on dangerous planet can generate exotic resources")
    void discover_dangerousPlanet_exoticPool() {
        when(planetService.add(any(Planet.class))).thenAnswer(inv -> inv.getArgument(0));
        ExplorationService service = new ExplorationService(planetService, new Random(1));

        PlanetDiscoveryResponse response = service.discover(new PlanetDiscoveryRequest("Zorion-X", 2000, 9));

        assertNotNull(response.resources());
        assertTrue(FULL_POOL.containsAll(response.resources()));
        assertTrue(response.resources().size() >= 1 && response.resources().size() <= 3);
    }
}