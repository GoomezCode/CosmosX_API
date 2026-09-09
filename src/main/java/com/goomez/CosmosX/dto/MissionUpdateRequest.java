package com.goomez.CosmosX.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record MissionUpdateRequest(
    @NotNull Long planetId,
    @NotEmpty List<Long> astronauts,
    @NotBlank String status
) {}