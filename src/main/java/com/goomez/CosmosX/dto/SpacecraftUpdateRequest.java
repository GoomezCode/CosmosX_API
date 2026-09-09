package com.goomez.CosmosX.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SpacecraftUpdateRequest(
    @NotBlank String name,
    @Min(0) int fuel,
    @Min(1) int capacity,
    @NotBlank String status
) {}