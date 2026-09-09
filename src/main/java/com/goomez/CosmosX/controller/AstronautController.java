package com.goomez.CosmosX.controller;

import com.goomez.CosmosX.dto.AstronautRequest;
import com.goomez.CosmosX.dto.AstronautResponse;
import com.goomez.CosmosX.dto.AstronautUpdateRequest;
import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.service.AstronautService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/astronauts")
public class AstronautController {
    private final AstronautService service;

    public AstronautController(AstronautService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AstronautResponse>> listAll() {
        List<AstronautResponse> response = service.listAll().stream()
            .map(a -> new AstronautResponse(a.getId(), a.getName(), a.getRank(), a.getExperience()))
            .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AstronautResponse> listById(@PathVariable Long id) {
        Astronaut astronaut = service.listById(id);
        return ResponseEntity.ok(new AstronautResponse(
            astronaut.getId(), astronaut.getName(), astronaut.getRank(), astronaut.getExperience()
        ));
    }

    @PostMapping
    public ResponseEntity<AstronautResponse> create(@Valid @RequestBody AstronautRequest request) {
        Astronaut astronaut = new Astronaut(0, request.name(), request.rank(), request.experience());
        Astronaut saved = service.add(astronaut);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AstronautResponse(
            saved.getId(), saved.getName(), saved.getRank(), saved.getExperience()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AstronautResponse> update(@PathVariable Long id, @Valid @RequestBody AstronautUpdateRequest request) {
        Astronaut astronaut = new Astronaut(id, request.name(), request.rank(), request.experience());
        Astronaut updated = service.update(id, astronaut);
        return ResponseEntity.ok(new AstronautResponse(
            updated.getId(), updated.getName(), updated.getRank(), updated.getExperience()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}