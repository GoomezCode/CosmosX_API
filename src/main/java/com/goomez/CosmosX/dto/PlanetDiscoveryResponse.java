package com.goomez.CosmosX.dto;

import java.util.List;

public record PlanetDiscoveryResponse(
    Long id,
    String name,
    int distance,
    int dangerLevel,
    List<String> resources,
    String discoveredAt
) {}