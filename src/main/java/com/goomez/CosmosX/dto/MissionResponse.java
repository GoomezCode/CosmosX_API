package com.goomez.CosmosX.dto;

import java.util.List;

public record MissionResponse(
    Long id,
    Long spacecraftId,
    Long planetId,
    List<Long> astronauts,
    String status
) {}