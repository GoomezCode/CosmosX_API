package com.goomez.CosmosX.dto;

import java.util.List;

public record PlanetResponse(
    Long id,
    String name,
    int distance,
    int dangerLevel,
    List<String> resources
) {}
