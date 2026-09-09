package com.goomez.CosmosX.service;

import com.goomez.CosmosX.model.Planet;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Service
public class PlanetService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo = new File("src/main/resources/data/planet.json");

    public List<Planet> listAll() {
        try {
            return mapper.readValue(arquivo, new TypeReference<List<Planet>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading planet data", e);
        }
    }

    public Planet add(Planet newPlanet) {
        try {
            List<Planet> planets = listAll();
            planets.add(newPlanet);
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, planets);
            return newPlanet;
        } catch (Exception e) {
            throw new RuntimeException("Error saving planet", e);
        }
    }
}
