package com.goomez.CosmosX.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PlanetRequest(
    @NotBlank String name,
    @Min(0) int distance,
    @Min(0) int dangerLevel,
    List<String> resources
) {}
