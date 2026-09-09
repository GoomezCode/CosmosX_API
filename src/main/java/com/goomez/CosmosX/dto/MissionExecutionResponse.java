package com.goomez.CosmosX.dto;

import java.util.List;

public record MissionExecutionResponse(
    Long missionId,
    String status,
    int fuelConsumed,
    List<ResourceFoundResponse> resourcesFound,
    List<String> events
) {}