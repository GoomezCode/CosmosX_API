package com.goomez.CosmosX.Service;

import com.goomez.CosmosX.Model.planetModel;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Service
public class planetService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo = new File("src/main/java/com/goomez/CosmosX/Data/planet.json");

    public List<planetModel> listAll() throws Exception {
        return mapper.readValue(
                arquivo,
                new TypeReference<List<planetModel>>() {}
        );
    }

    public planetModel add(planetModel newPlanet) throws Exception{
        List<planetModel> planet = listAll();

        planet.add(newPlanet);

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(arquivo, planet);

        return newPlanet;
    }
}
