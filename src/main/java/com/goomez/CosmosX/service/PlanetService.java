package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Planet;
import com.goomez.CosmosX.repository.PlanetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanetService {
    private final PlanetRepository planetRepository;

    public PlanetService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    public List<Planet> listAll() {
        return planetRepository.findAll();
    }

    public Planet listById(long id) {
        return planetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Planet not found with id: " + id));
    }

    @Transactional
    public Planet add(Planet newPlanet) {
        return planetRepository.save(newPlanet);
    }

    @Transactional
    public Planet update(long id, Planet updatedPlanet) {
        Planet existing = listById(id);
        existing.setName(updatedPlanet.getName());
        existing.setDistance(updatedPlanet.getDistance());
        existing.setDangerLevel(updatedPlanet.getDangerLevel());
        existing.setResources(updatedPlanet.getResources());
        return planetRepository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!planetRepository.existsById(id)) {
            return false;
        }
        planetRepository.deleteById(id);
        return true;
    }
}