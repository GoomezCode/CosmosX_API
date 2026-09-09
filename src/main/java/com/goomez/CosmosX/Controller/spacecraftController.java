package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.SpacecraftRequest;
import com.goomez.CosmosX.dto.SpacecraftResponse;
import com.goomez.CosmosX.dto.SpacecraftUpdateRequest;
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

    @GetMapping("/{id}")
    public ResponseEntity<SpacecraftResponse> listById(@PathVariable Long id) {
        Spacecraft spacecraft = service.listById(id);
        return ResponseEntity.ok(new SpacecraftResponse(
            spacecraft.getId(), spacecraft.getName(), spacecraft.getFuel(), spacecraft.getCapacity(), spacecraft.getStatus()
        ));
    }

    @PostMapping
    public ResponseEntity<SpacecraftResponse> create(@Valid @RequestBody SpacecraftRequest request) {
        Spacecraft spacecraft = new Spacecraft(0, request.name(), request.fuel(), request.capacity(), request.status());
        Spacecraft saved = service.add(spacecraft);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SpacecraftResponse(
            saved.getId(), saved.getName(), saved.getFuel(), saved.getCapacity(), saved.getStatus()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpacecraftResponse> update(@PathVariable Long id, @Valid @RequestBody SpacecraftUpdateRequest request) {
        Spacecraft spacecraft = new Spacecraft(id, request.name(), request.fuel(), request.capacity(), request.status());
        Spacecraft updated = service.update(id, spacecraft);
        return ResponseEntity.ok(new SpacecraftResponse(
            updated.getId(), updated.getName(), updated.getFuel(), updated.getCapacity(), updated.getStatus()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}