package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.ResourceFoundResponse;
import com.goomez.CosmosX.model.Planet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ResourceServiceTest {

    @Test
    @DisplayName("generate returns one entry per planet resource with quantity >= 1")
    void generate_returnsResources() {
        ResourceService service = new ResourceService(new FixedRandom(0));
        Planet planet = new Planet(1, "Zorion", 500, 3, List.of("Gold", "Water"));

        List<ResourceFoundResponse> result = service.generate(planet);

        assertEquals(2, result.size());
        assertEquals("Gold", result.get(0).resource());
        assertEquals(1, result.get(0).quantity());
        assertEquals("Water", result.get(1).resource());
        assertEquals(1, result.get(1).quantity());
    }

    @Test
    @DisplayName("generate caps quantity at 50")
    void generate_maxQuantity() {
        ResourceService service = new ResourceService(new FixedRandom(49));
        Planet planet = new Planet(1, "Zorion", 500, 3, List.of("Gold"));

        List<ResourceFoundResponse> result = service.generate(planet);

        assertEquals(50, result.get(0).quantity());
    }

    @Test
    @DisplayName("generate returns empty list for null resources")
    void generate_nullResources_empty() {
        ResourceService service = new ResourceService(new FixedRandom(0));
        Planet planet = new Planet(1, "Zorion", 500, 3, null);

        assertTrue(service.generate(planet).isEmpty());
    }

    @Test
    @DisplayName("generate returns empty list for empty resources")
    void generate_emptyResources_empty() {
        ResourceService service = new ResourceService(new FixedRandom(0));
        Planet planet = new Planet(1, "Zorion", 500, 3, List.of());

        assertTrue(service.generate(planet).isEmpty());
    }

    private static class FixedRandom extends Random {
        private final int value;

        FixedRandom(int value) {
            this.value = value;
        }

        @Override
        public int nextInt(int bound) {
            return value;
        }
    }
}