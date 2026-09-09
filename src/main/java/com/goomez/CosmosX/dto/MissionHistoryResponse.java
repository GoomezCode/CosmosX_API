package com.goomez.CosmosX.dto;

import java.util.List;

public record MissionHistoryResponse(
    Long id,
    String planetName,
    List<String> astronauts,
    String status,
    int fuelConsumed,
    List<String> resourcesFound,
    String completedAt
) {}