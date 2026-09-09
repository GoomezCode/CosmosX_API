package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.StatsResponse;
import com.goomez.CosmosX.model.Mission;
import com.goomez.CosmosX.model.ResourceFound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatsServiceTest {

    private final MissionService missionService = mock(MissionService.class);
    private final StatsService service = new StatsService(missionService);

    @Test
    @DisplayName("getStats aggregates totals, rate and resources")
    void getStats_aggregates() {
        Mission success1 = new Mission(1, 1, 1, List.of(1L), "SUCCESS");
        success1.setResourcesFound(List.of(
            new ResourceFound("Iron", 25),
            new ResourceFound("Water", 50)
        ));
        Mission success2 = new Mission(2, 1, 2, List.of(1L), "SUCCESS");
        success2.setResourcesFound(List.of(new ResourceFound("Gold", 10)));
        Mission failed = new Mission(3, 1, 1, List.of(1L), "FAILED");
        failed.setResourcesFound(List.of(new ResourceFound("Iron", 99)));
        Mission pending = new Mission(4, 1, 1, List.of(1L), "PENDING");

        when(missionService.listAll()).thenReturn(List.of(success1, success2, failed, pending));

        StatsResponse response = service.getStats();

        assertEquals(4, response.totalMissions());
        assertEquals(2, response.successes());
        assertEquals(1, response.failures());
        assertEquals(50.0, response.successRate());
        assertEquals(85, response.resourcesCollected());
        assertEquals("Water", response.topResource());
    }

    @Test
    @DisplayName("getStats ignores resources from failed missions")
    void getStats_ignoresFailedResources() {
        Mission failed = new Mission(1, 1, 1, List.of(1L), "FAILED");
        failed.setResourcesFound(List.of(new ResourceFound("Platinum", 1000)));

        when(missionService.listAll()).thenReturn(List.of(failed));

        StatsResponse response = service.getStats();

        assertEquals(0, response.resourcesCollected());
        assertNull(response.topResource());
    }

    @Test
    @DisplayName("getStats returns zeros for empty mission list")
    void getStats_emptyList() {
        when(missionService.listAll()).thenReturn(List.of());

        StatsResponse response = service.getStats();

        assertEquals(0, response.totalMissions());
        assertEquals(0, response.successes());
        assertEquals(0, response.failures());
        assertEquals(0.0, response.successRate());
        assertEquals(0, response.resourcesCollected());
        assertNull(response.topResource());
    }
}