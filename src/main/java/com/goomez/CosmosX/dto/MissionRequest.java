package com.goomez.CosmosX.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MissionRequest(
    @NotNull Long spacecraftId,
    @NotNull Long planetId,
    @NotEmpty List<Long> astronauts
) {}