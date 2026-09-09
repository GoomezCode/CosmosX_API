package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.PlanetRequest;
import com.goomez.CosmosX.dto.PlanetResponse;
import com.goomez.CosmosX.dto.PlanetUpdateRequest;
import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.service.PlanetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planet")
public class PlanetController {
    private final PlanetService service;

    public PlanetController(PlanetService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PlanetResponse>> listAll() {
        List<PlanetResponse> response = service.listAll().stream()
            .map(p -> new PlanetResponse(p.getId(), p.getName(), p.getDistance(), p.getDangerLevel(), p.getResources()))
            .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanetResponse> listById(@PathVariable Long id) {
        Planet planet = service.listById(id);
        return ResponseEntity.ok(new PlanetResponse(
            planet.getId(), planet.getName(), planet.getDistance(), planet.getDangerLevel(), planet.getResources()
        ));
    }

    @PostMapping
    public ResponseEntity<PlanetResponse> create(@Valid @RequestBody PlanetRequest request) {
        Planet planet = new Planet(0, request.name(), request.distance(), request.dangerLevel(), request.resources());
        Planet saved = service.add(planet);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PlanetResponse(
            saved.getId(), saved.getName(), saved.getDistance(), saved.getDangerLevel(), saved.getResources()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanetResponse> update(@PathVariable Long id, @Valid @RequestBody PlanetUpdateRequest request) {
        Planet planet = new Planet(id, request.name(), request.distance(), request.dangerLevel(), request.resources());
        Planet updated = service.update(id, planet);
        return ResponseEntity.ok(new PlanetResponse(
            updated.getId(), updated.getName(), updated.getDistance(), updated.getDangerLevel(), updated.getResources()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}