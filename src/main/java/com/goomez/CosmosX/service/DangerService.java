package com.goomez.CosmosX.service;

import com.goomez.CosmosX.model.MissionEvent;
import com.goomez.CosmosX.model.Planet;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class DangerService {
    private final Random random;

    public DangerService() {
        this.random = new Random();
    }

    DangerService(Random random) {
        this.random = random;
    }

    public MissionEvent resolveEvent(Planet planet) {
        int successChance = Math.max(0, 60 - planet.getDangerLevel() * 5);
        int roll = random.nextInt(100) + 1;
        if (roll <= successChance) {
            return MissionEvent.SUCCESS;
        }
        if (roll <= successChance + 15) {
            return MissionEvent.MECHANICAL_FAILURE;
        }
        if (roll <= successChance + 30) {
            return MissionEvent.ALIEN_ATTACK;
        }
        return MissionEvent.COSMIC_STORM;
    }
}