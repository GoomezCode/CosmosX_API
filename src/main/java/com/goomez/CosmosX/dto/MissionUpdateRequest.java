package com.goomez.CosmosX.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MissionUpdateRequest(
    @NotNull Long spacecraftId,
    @NotNull Long planetId,
    @NotEmpty List<Long> astronauts,
    @NotBlank String status
) {}