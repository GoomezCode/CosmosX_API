package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.InsufficientFuelException;
import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.model.Spacecraft;
import org.springframework.stereotype.Service;

@Service
public class FuelService {

    public int calculateConsumption(Spacecraft spacecraft, Planet planet) {
        if (spacecraft.getFuel() < planet.getDistance()) {
            throw new InsufficientFuelException("Fuel too low for this mission");
        }
        return planet.getDistance() * planet.getDangerLevel();
    }
}