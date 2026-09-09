package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Astronaut;
import com.goomez.CosmosX.repository.AstronautRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AstronautService {
    private final AstronautRepository astronautRepository;

    public AstronautService(AstronautRepository astronautRepository) {
        this.astronautRepository = astronautRepository;
    }

    public List<Astronaut> listAll() {
        return astronautRepository.findAll();
    }

    public Astronaut listById(long id) {
        return astronautRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Astronaut not found with id: " + id));
    }

    @Transactional
    public Astronaut add(Astronaut newAstronaut) {
        return astronautRepository.save(newAstronaut);
    }

    @Transactional
    public Astronaut update(long id, Astronaut updatedAstronaut) {
        Astronaut existing = listById(id);
        existing.setName(updatedAstronaut.getName());
        existing.setRank(updatedAstronaut.getRank());
        existing.setExperience(updatedAstronaut.getExperience());
        return astronautRepository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!astronautRepository.existsById(id)) {
            return false;
        }
        astronautRepository.deleteById(id);
        return true;
    }
}