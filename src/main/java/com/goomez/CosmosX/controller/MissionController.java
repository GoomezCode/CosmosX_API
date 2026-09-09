package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.MissionExecutionResponse;
import com.goomez.CosmosX.dto.MissionRequest;
import com.goomez.CosmosX.dto.MissionResponse;
import com.goomez.CosmosX.dto.MissionUpdateRequest;
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
            .map(m -> new MissionResponse(m.getId(), m.getSpacecraftId(), m.getPlanetId(), m.getAstronauts(), m.getStatus()))
            .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionResponse> listById(@PathVariable Long id) {
        Mission mission = service.listById(id);
        return ResponseEntity.ok(new MissionResponse(
            mission.getId(), mission.getSpacecraftId(), mission.getPlanetId(), mission.getAstronauts(), mission.getStatus()
        ));
    }

    @PostMapping
    public ResponseEntity<MissionResponse> create(@Valid @RequestBody MissionRequest request) {
        Mission mission = new Mission(0, request.spacecraftId(), request.planetId(), request.astronauts(), "PENDING");
        Mission saved = service.add(mission);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MissionResponse(
            saved.getId(), saved.getSpacecraftId(), saved.getPlanetId(), saved.getAstronauts(), saved.getStatus()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionResponse> update(@PathVariable Long id, @Valid @RequestBody MissionUpdateRequest request) {
        Mission mission = new Mission(id, request.spacecraftId(), request.planetId(), request.astronauts(), request.status());
        Mission updated = service.update(id, mission);
        return ResponseEntity.ok(new MissionResponse(
            updated.getId(), updated.getSpacecraftId(), updated.getPlanetId(), updated.getAstronauts(), updated.getStatus()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<MissionExecutionResponse> start(@PathVariable Long id) {
        return ResponseEntity.ok(service.executeMission(id));
    }
}