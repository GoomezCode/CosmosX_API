package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Planet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PlanetService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo;

    public PlanetService(@Value("${app.data.path:src/main/resources/data}") String dataPath) {
        this.arquivo = Paths.get(dataPath, "planet.json").toFile();
    }

    public List<Planet> listAll() {
        try {
            return mapper.readValue(arquivo, new TypeReference<List<Planet>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading planet data", e);
        }
    }

    public Planet listById(long id) {
        return listAll().stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Planet not found with id: " + id));
    }

    public Planet add(Planet newPlanet) {
        try {
            List<Planet> planets = listAll();
            long nextId = planets.stream().mapToLong(Planet::getId).max().orElse(0) + 1;
            newPlanet.setId(nextId);
            planets.add(newPlanet);
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, planets);
            return newPlanet;
        } catch (Exception e) {
            throw new RuntimeException("Error saving planet", e);
        }
    }

    public Planet update(long id, Planet updatedPlanet) {
        try {
            List<Planet> planets = listAll();
            Planet existing = planets.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found with id: " + id));
            existing.setName(updatedPlanet.getName());
            existing.setDistance(updatedPlanet.getDistance());
            existing.setDangerLevel(updatedPlanet.getDangerLevel());
            existing.setResources(updatedPlanet.getResources());
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, planets);
            return existing;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating planet", e);
        }
    }

    public boolean delete(Long id) {
        try {
            List<Planet> planets = listAll();
            boolean removed = planets.removeIf(p -> p.getId() == id);
            if (removed) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, planets);
            }
            return removed;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting planet", e);
        }
    }
}