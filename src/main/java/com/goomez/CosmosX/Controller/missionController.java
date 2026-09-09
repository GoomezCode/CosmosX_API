package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.MissionRequest;
import com.goomez.CosmosX.dto.MissionResponse;
import com.goomez.CosmosX.model.Mission;
import com.goomez.CosmosX.service.MissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mission")
public class MissionController {
    private final MissionService service;

    public MissionController(MissionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MissionResponse>> listAll() {
        List<MissionResponse> response = service.listAll().stream()
            .map(m -> new MissionResponse(m.getId(), m.getPlanetId(), m.getAstronauts(), m.getStatus()))
            .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MissionResponse> create(@Valid @RequestBody MissionRequest request) {
        Mission mission = new Mission(0, request.planetId(), request.astronauts(), "PENDING");
        Mission saved = service.add(mission);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MissionResponse(
            saved.getId(), saved.getPlanetId(), saved.getAstronauts(), saved.getStatus()
        ));
    }
}
