package com.goomez.CosmosX.dto;

public record SpacecraftResponse(
    Long id,
    String name,
    int fuel,
    int capacity,
    String status
) {}
