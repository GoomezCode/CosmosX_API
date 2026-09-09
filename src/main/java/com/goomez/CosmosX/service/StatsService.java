package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.StatsResponse;
import com.goomez.CosmosX.model.Mission;
import com.goomez.CosmosX.model.ResourceFound;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {
    private final MissionService missionService;

    public StatsService(MissionService missionService) {
        this.missionService = missionService;
    }

    public StatsResponse getStats() {
        List<Mission> missions = missionService.listAll();

        long total = missions.size();
        long successes = missions.stream().filter(m -> "SUCCESS".equals(m.getStatus())).count();
        long failures = missions.stream().filter(m -> "FAILED".equals(m.getStatus())).count();
        double successRate = total == 0 ? 0 : Math.round((successes * 10000.0) / total) / 100.0;
        long resourcesCollected = missions.stream()
            .filter(m -> "SUCCESS".equals(m.getStatus()))
            .filter(m -> m.getResourcesFound() != null)
            .flatMap(m -> m.getResourcesFound().stream())
            .mapToLong(ResourceFound::getQuantity)
            .sum();

        return new StatsResponse(total, successes, failures, successRate, resourcesCollected, computeTopResource(missions));
    }

    private String computeTopResource(List<Mission> missions) {
        Map<String, Long> totals = new HashMap<>();
        for (Mission mission : missions) {
            if (!"SUCCESS".equals(mission.getStatus()) || mission.getResourcesFound() == null) {
                continue;
            }
            for (ResourceFound resource : mission.getResourcesFound()) {
                totals.merge(resource.getResource(), (long) resource.getQuantity(), Long::sum);
            }
        }
        return totals.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}