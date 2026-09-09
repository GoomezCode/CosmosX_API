package com.goomez.CosmosX.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PlanetDiscoveryRequest(
    @NotBlank String name,
    @Min(0) int distance,
    @Min(0) @Max(10) int dangerLevel
) {}