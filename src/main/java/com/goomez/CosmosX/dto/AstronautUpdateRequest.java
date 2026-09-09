package com.goomez.CosmosX.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AstronautUpdateRequest(
    @NotBlank String name,
    @NotBlank String rank,
    @Min(0) int experience
) {}