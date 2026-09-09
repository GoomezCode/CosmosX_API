package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.InsufficientFuelException;
import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.model.Spacecraft;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuelServiceTest {

    private final FuelService service = new FuelService();

    @Test
    @DisplayName("calculateConsumption returns distance * dangerLevel for sufficient fuel")
    void calculateConsumption_returnsProduct() {
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 1000, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 3, List.of("Iron"));

        int consumption = service.calculateConsumption(spacecraft, planet);

        assertEquals(1500, consumption);
    }

    @Test
    @DisplayName("calculateConsumption accepts fuel exactly equal to distance")
    void calculateConsumption_exactFuel_ok() {
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 500, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 2, List.of("Iron"));

        assertEquals(1000, service.calculateConsumption(spacecraft, planet));
    }

    @Test
    @DisplayName("calculateConsumption throws InsufficientFuelException when fuel is below distance")
    void calculateConsumption_insufficientFuel_throws() {
        Spacecraft spacecraft = new Spacecraft(1, "Falcon-X", 400, 5, "READY");
        Planet planet = new Planet(1, "Zorion", 500, 3, List.of("Iron"));

        assertThrows(InsufficientFuelException.class, () -> service.calculateConsumption(spacecraft, planet));
    }
}