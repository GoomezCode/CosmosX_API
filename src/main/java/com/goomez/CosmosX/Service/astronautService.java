package com.goomez.CosmosX.Service;

import com.goomez.CosmosX.Model.astronautModel;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Service
public class astronautService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo = new File("src/main/java/com/goomez/CosmosX/Data/astronaut.json");

    public List<astronautModel> listAll() throws Exception {
        return mapper.readValue(
                arquivo,
                new TypeReference<List<astronautModel>>() {}
        );
    }

    public astronautModel listId(long id) throws Exception{
        List<astronautModel> astronaut = listAll();
        return  astronaut.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public astronautModel add(astronautModel newAstronaut) throws Exception{
        List<astronautModel> astronauts = listAll();

        astronauts.add(newAstronaut);

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(arquivo, astronauts);

        return newAstronaut;
    }

    public boolean delete(Long id) throws Exception{
        List<astronautModel> astronauts = listAll();

        boolean removido = astronauts.removeIf(
                user -> user.getId() == id
        );

        if (removido){
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(arquivo, astronauts);
        }
        return removido;
    }
}
