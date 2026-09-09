package com.goomez.CosmosX.dto;

import java.util.List;

public record MissionResponse(
    Long id,
    Long planetId,
    List<Long> astronauts,
    String status
) {}
