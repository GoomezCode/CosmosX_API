package com.goomez.CosmosX.service;

import com.goomez.CosmosX.model.MissionEvent;
import com.goomez.CosmosX.model.Planet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DangerServiceTest {

    @Test
    @DisplayName("low roll on safe planet results in SUCCESS")
    void resolveEvent_successOnSafePlanet() {
        DangerService service = new DangerService(new FixedRandom(0));
        Planet planet = new Planet(1, "Zorion", 500, 0, List.of("Iron"));

        assertEquals(MissionEvent.SUCCESS, service.resolveEvent(planet));
    }

    @Test
    @DisplayName("roll at success boundary still succeeds")
    void resolveEvent_successBoundary() {
        DangerService service = new DangerService(new FixedRandom(59));
        Planet planet = new Planet(1, "Zorion", 500, 0, List.of("Iron"));

        assertEquals(MissionEvent.SUCCESS, service.resolveEvent(planet));
    }

    @Test
    @DisplayName("roll just above success window is MECHANICAL_FAILURE")
    void resolveEvent_mechanicalFailure() {
        DangerService service = new DangerService(new FixedRandom(60));
        Planet planet = new Planet(1, "Zorion", 500, 0, List.of("Iron"));

        assertEquals(MissionEvent.MECHANICAL_FAILURE, service.resolveEvent(planet));
    }

    @Test
    @DisplayName("roll in alien window is ALIEN_ATTACK")
    void resolveEvent_alienAttack() {
        DangerService service = new DangerService(new FixedRandom(75));
        Planet planet = new Planet(1, "Zorion", 500, 0, List.of("Iron"));

        assertEquals(MissionEvent.ALIEN_ATTACK, service.resolveEvent(planet));
    }

    @Test
    @DisplayName("roll above alien window is COSMIC_STORM")
    void resolveEvent_cosmicStorm() {
        DangerService service = new DangerService(new FixedRandom(90));
        Planet planet = new Planet(1, "Zorion", 500, 0, List.of("Iron"));

        assertEquals(MissionEvent.COSMIC_STORM, service.resolveEvent(planet));
    }

    @Test
    @DisplayName("high danger planet reduces success chance")
    void resolveEvent_highDanger_reducesSuccess() {
        DangerService service = new DangerService(new FixedRandom(20));
        Planet planet = new Planet(1, "Zorion", 500, 10, List.of("Iron"));

        assertEquals(MissionEvent.MECHANICAL_FAILURE, service.resolveEvent(planet));
    }

    @Test
    @DisplayName("danger above 12 clamps success chance to zero")
    void resolveEvent_veryHighDanger_neverSuccessOnLowRoll() {
        DangerService service = new DangerService(new FixedRandom(0));
        Planet planet = new Planet(1, "Zorion", 500, 12, List.of("Iron"));

        assertEquals(MissionEvent.MECHANICAL_FAILURE, service.resolveEvent(planet));
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