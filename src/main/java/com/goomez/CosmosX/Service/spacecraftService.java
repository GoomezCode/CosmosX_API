package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Spacecraft;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Service
public class SpacecraftService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo = new File("src/main/resources/data/spacecraft.json");

    public List<Spacecraft> listAll() {
        try {
            return mapper.readValue(arquivo, new TypeReference<List<Spacecraft>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading spacecraft data", e);
        }
    }

    public Spacecraft listById(long id) {
        return listAll().stream()
            .filter(s -> s.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Spacecraft not found with id: " + id));
    }

    public Spacecraft add(Spacecraft newSpacecraft) {
        try {
            List<Spacecraft> spacecraft = listAll();
            long nextId = spacecraft.stream().mapToLong(Spacecraft::getId).max().orElse(0) + 1;
            newSpacecraft.setId(nextId);
            spacecraft.add(newSpacecraft);
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, spacecraft);
            return newSpacecraft;
        } catch (Exception e) {
            throw new RuntimeException("Error saving spacecraft", e);
        }
    }

    public Spacecraft update(long id, Spacecraft updatedSpacecraft) {
        try {
            List<Spacecraft> spacecraft = listAll();
            Spacecraft existing = spacecraft.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Spacecraft not found with id: " + id));
            existing.setName(updatedSpacecraft.getName());
            existing.setFuel(updatedSpacecraft.getFuel());
            existing.setCapacity(updatedSpacecraft.getCapacity());
            existing.setStatus(updatedSpacecraft.getStatus());
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, spacecraft);
            return existing;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating spacecraft", e);
        }
    }

    public boolean delete(Long id) {
        try {
            List<Spacecraft> spacecraft = listAll();
            boolean removed = spacecraft.removeIf(s -> s.getId() == id);
            if (removed) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, spacecraft);
            }
            return removed;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting spacecraft", e);
        }
    }
}