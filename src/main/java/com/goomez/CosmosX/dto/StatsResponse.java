package com.goomez.CosmosX.dto;

public record StatsResponse(
    long totalMissions,
    long successes,
    long failures,
    double successRate,
    long resourcesCollected,
    String topResource
) {}