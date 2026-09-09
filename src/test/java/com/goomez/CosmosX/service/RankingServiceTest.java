package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.AstronautRankingResponse;
import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.model.Mission;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RankingServiceTest {

    private final AstronautService astronautService = mock(AstronautService.class);
    private final MissionService missionService = mock(MissionService.class);
    private final RankingService service = new RankingService(astronautService, missionService);

    @Test
    @DisplayName("getRanking counts completed missions and sorts by experience desc")
    void getRanking_countsAndSorts() {
        when(astronautService.listAll()).thenReturn(List.of(
            new Astronaut(1, "Daniel", "Commander", 1200),
            new Astronaut(2, "Laura", "Pilot", 800)
        ));
        when(missionService.listAll()).thenReturn(List.of(
            new Mission(1, 1, 1, List.of(1L, 2L), "SUCCESS"),
            new Mission(2, 1, 2, List.of(1L), "SUCCESS"),
            new Mission(3, 1, 1, List.of(1L), "FAILED")
        ));

        List<AstronautRankingResponse> ranking = service.getRanking();

        assertEquals(2, ranking.size());
        assertEquals("Daniel", ranking.get(0).name());
        assertEquals(2, ranking.get(0).missionsCompleted());
        assertEquals("Laura", ranking.get(1).name());
        assertEquals(1, ranking.get(1).missionsCompleted());
    }

    @Test
    @DisplayName("getRanking returns zero completed missions when none exist")
    void getRanking_noMissions() {
        when(astronautService.listAll()).thenReturn(List.of(
            new Astronaut(1, "Daniel", "Commander", 0)
        ));
        when(missionService.listAll()).thenReturn(List.of());

        List<AstronautRankingResponse> ranking = service.getRanking();

        assertEquals(1, ranking.size());
        assertEquals(0, ranking.get(0).missionsCompleted());
    }
}