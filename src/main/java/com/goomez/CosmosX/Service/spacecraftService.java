package com.goomez.CosmosX.service;

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

    public Spacecraft add(Spacecraft newSpacecraft) {
        try {
            List<Spacecraft> spacecraft = listAll();
            spacecraft.add(newSpacecraft);
            mapper.writerWithDefaultPrettyPrinter().writeValue(arquivo, spacecraft);
            return newSpacecraft;
        } catch (Exception e) {
            throw new RuntimeException("Error saving spacecraft", e);
        }
    }
}
