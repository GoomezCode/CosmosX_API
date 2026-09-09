package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Spacecraft;
import com.goomez.CosmosX.repository.SpacecraftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpacecraftService {
    private final SpacecraftRepository spacecraftRepository;

    public SpacecraftService(SpacecraftRepository spacecraftRepository) {
        this.spacecraftRepository = spacecraftRepository;
    }

    public List<Spacecraft> listAll() {
        return spacecraftRepository.findAll();
    }

    public Spacecraft listById(long id) {
        return spacecraftRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Spacecraft not found with id: " + id));
    }

    @Transactional
    public Spacecraft add(Spacecraft newSpacecraft) {
        return spacecraftRepository.save(newSpacecraft);
    }

    @Transactional
    public Spacecraft update(long id, Spacecraft updatedSpacecraft) {
        Spacecraft existing = listById(id);
        existing.setName(updatedSpacecraft.getName());
        existing.setFuel(updatedSpacecraft.getFuel());
        existing.setCapacity(updatedSpacecraft.getCapacity());
        existing.setStatus(updatedSpacecraft.getStatus());
        return spacecraftRepository.save(existing);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!spacecraftRepository.existsById(id)) {
            return false;
        }
        spacecraftRepository.deleteById(id);
        return true;
    }
}