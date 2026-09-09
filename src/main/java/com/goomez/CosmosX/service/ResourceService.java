package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.ResourceFoundResponse;
import com.goomez.CosmosX.model.Planet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ResourceService {
    private final Random random;

    public ResourceService() {
        this.random = new Random();
    }

    ResourceService(Random random) {
        this.random = random;
    }

    public List<ResourceFoundResponse> generate(Planet planet) {
        List<String> resources = planet.getResources();
        if (resources == null || resources.isEmpty()) {
            return List.of();
        }
        return resources.stream()
            .map(r -> new ResourceFoundResponse(r, random.nextInt(50) + 1))
            .toList();
    }
}