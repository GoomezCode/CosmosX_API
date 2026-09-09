package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.AstronautRankingResponse;
import com.goomez.CosmosX.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RankingController {
    private final RankingService service;

    public RankingController(RankingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AstronautRankingResponse>> getRanking() {
        return ResponseEntity.ok(service.getRanking());
    }
}