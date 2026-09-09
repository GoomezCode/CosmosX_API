package com.goomez.CosmosX.service;

import com.goomez.CosmosX.exception.ResourceNotFoundException;
import com.goomez.CosmosX.model.Astronaut;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Service
public class AstronautService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo = new File("src/main/resources/data/astronaut.json");

    public List<Astronaut> listAll() {
        try {
            return mapper.readValue(arquivo, new TypeReference<List<Astronaut>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error reading astronauts data", e);
        }
    }

    public Astronaut listById(long id) {
        return listAll().stream()
            .filter(a -> a.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Astronaut not found with id: " + id));
    }

    public Astronaut add(Astronaut newAstronaut) {
        try {
            List<Astronaut> astronauts = listAll();
            astronauts.add(newAstronaut);
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, astronauts);
            return newAstronaut;
        } catch (Exception e) {
            throw new RuntimeException("Error saving astronaut", e);
        }
    }

    public boolean delete(Long id) {
        try {
            List<Astronaut> astronauts = listAll();
            boolean removed = astronauts.removeIf(a -> a.getId() == id);
            if (removed) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, astronauts);
            }
            return removed;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting astronaut", e);
        }
    }
}
