package com.goomez.CosmosX.config;

import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.model.Mission;
import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.model.ResourceFound;
import com.goomez.CosmosX.model.Spacecraft;
import com.goomez.CosmosX.repository.AstronautRepository;
import com.goomez.CosmosX.repository.MissionRepository;
import com.goomez.CosmosX.repository.PlanetRepository;
import com.goomez.CosmosX.repository.SpacecraftRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final AstronautRepository astronautRepository;
    private final SpacecraftRepository spacecraftRepository;
    private final PlanetRepository planetRepository;
    private final MissionRepository missionRepository;

    public DataSeeder(AstronautRepository astronautRepository,
                      SpacecraftRepository spacecraftRepository,
                      PlanetRepository planetRepository,
                      MissionRepository missionRepository) {
        this.astronautRepository = astronautRepository;
        this.spacecraftRepository = spacecraftRepository;
        this.planetRepository = planetRepository;
        this.missionRepository = missionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (astronautRepository.count() > 0) {
            return;
        }

        Astronaut daniel = new Astronaut(0, "Daniel", "Commander", 1200);
        Astronaut laura = new Astronaut(0, "Laura", "Pilot", 800);
        astronautRepository.saveAll(List.of(daniel, laura));

        Spacecraft falcon = new Spacecraft(0, "Falcon-X", 1000, 5, "READY");
        spacecraftRepository.save(falcon);

        Planet zorion = new Planet(0, "Zorion", 500, 3, List.of("Iron", "Water"));
        planetRepository.save(zorion);

        Mission pending = new Mission(0, falcon.getId(), zorion.getId(), List.of(daniel.getId(), laura.getId()), "PENDING");

        Mission completed = new Mission(0, falcon.getId(), zorion.getId(), List.of(daniel.getId()), "SUCCESS");
        completed.setFuelConsumed(500);
        completed.setResourcesFound(List.of(
            new ResourceFound("Iron", 25),
            new ResourceFound("Water", 50)
        ));
        completed.setCompletedAt("2026-09-09T14:30:00");

        missionRepository.saveAll(List.of(pending, completed));
    }
}