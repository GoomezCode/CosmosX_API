package com.goomez.CosmosX.Service;

import com.goomez.CosmosX.Model.spacecraftModel;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Service
public class spacecraftService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo = new File("src/main/java/com/goomez/CosmosX/Data/spacecraft.json");

    public List<spacecraftModel> listAll() throws Exception {
        return mapper.readValue(
                arquivo,
                new TypeReference<List<spacecraftModel>>() {}
        );
    }
    public spacecraftModel add(spacecraftModel newSpacecraft) throws Exception{
        List<spacecraftModel> spacecraft = listAll();

        spacecraft.add(newSpacecraft);

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(arquivo, spacecraft);

        return newSpacecraft;
    }
}
