package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.SpacecraftRequest;
import com.goomez.CosmosX.dto.SpacecraftResponse;
import com.goomez.CosmosX.model.Spacecraft;
import com.goomez.CosmosX.service.SpacecraftService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spacecraft")
public class SpacecraftController {
    private final SpacecraftService service;

    public SpacecraftController(SpacecraftService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SpacecraftResponse>> listAll() {
        List<SpacecraftResponse> response = service.listAll().stream()
            .map(s -> new SpacecraftResponse(s.getId(), s.getName(), s.getFuel(), s.getCapacity(), s.getStatus()))
            .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SpacecraftResponse> create(@Valid @RequestBody SpacecraftRequest request) {
        Spacecraft spacecraft = new Spacecraft(0, request.name(), request.fuel(), request.capacity(), request.status());
        Spacecraft saved = service.add(spacecraft);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SpacecraftResponse(
            saved.getId(), saved.getName(), saved.getFuel(), saved.getCapacity(), saved.getStatus()
        ));
    }
}
