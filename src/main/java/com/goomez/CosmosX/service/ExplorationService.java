package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.PlanetDiscoveryRequest;
import com.goomez.CosmosX.dto.PlanetDiscoveryResponse;
import com.goomez.CosmosX.model.Planet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class ExplorationService {
    private static final List<String> BASIC = List.of("Iron", "Water", "Stone");
    private static final List<String> ADVANCED = List.of("Gold", "Titanium", "Silicon");
    private static final List<String> EXOTIC = List.of("Crystal", "Platinum", "Uranium");

    private final PlanetService planetService;
    private final Random random;

    @Autowired
    public ExplorationService(PlanetService planetService) {
        this(planetService, new Random());
    }

    ExplorationService(PlanetService planetService, Random random) {
        this.planetService = planetService;
        this.random = random;
    }

    public PlanetDiscoveryResponse discover(PlanetDiscoveryRequest request) {
        Planet planet = new Planet(0, request.name(), request.distance(), request.dangerLevel(), generateResources(request.dangerLevel()));
        planet.setDiscoveredAt(LocalDateTime.now().toString());
        Planet saved = planetService.add(planet);
        return new PlanetDiscoveryResponse(
            saved.getId(),
            saved.getName(),
            saved.getDistance(),
            saved.getDangerLevel(),
            saved.getResources(),
            saved.getDiscoveredAt()
        );
    }

    private List<String> generateResources(int dangerLevel) {
        List<String> pool = new ArrayList<>(BASIC);
        if (dangerLevel >= 3) {
            pool.addAll(ADVANCED);
        }
        if (dangerLevel >= 6) {
            pool.addAll(EXOTIC);
        }
        Collections.shuffle(pool, random);
        int count = random.nextInt(3) + 1;
        return pool.subList(0, Math.min(count, pool.size()));
    }
}