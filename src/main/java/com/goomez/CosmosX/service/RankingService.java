package com.goomez.CosmosX.service;

import com.goomez.CosmosX.dto.AstronautRankingResponse;
import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.model.Mission;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RankingService {
    private final AstronautService astronautService;
    private final MissionService missionService;

    public RankingService(AstronautService astronautService, MissionService missionService) {
        this.astronautService = astronautService;
        this.missionService = missionService;
    }

    public List<AstronautRankingResponse> getRanking() {
        List<Mission> missions = missionService.listAll();
        return astronautService.listAll().stream()
            .map(a -> new AstronautRankingResponse(
                a.getName(),
                a.getRank(),
                a.getExperience(),
                missions.stream()
                    .filter(m -> "SUCCESS".equals(m.getStatus()))
                    .filter(m -> m.getAstronauts() != null && m.getAstronauts().contains(a.getId()))
                    .count()
            ))
            .sorted(Comparator.comparingInt(AstronautRankingResponse::experience).reversed())
            .toList();
    }
}