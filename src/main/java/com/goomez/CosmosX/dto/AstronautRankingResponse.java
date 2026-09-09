package com.goomez.CosmosX.dto;

public record AstronautRankingResponse(
    String name,
    String rank,
    int experience,
    long missionsCompleted
) {}