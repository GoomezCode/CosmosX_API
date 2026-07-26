package com.goomez.CosmosX.Service;

import com.goomez.CosmosX.Model.astronautModel;
import com.goomez.CosmosX.Model.missionModel;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Service
public class missionService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File arquivo = new File("src/main/java/com/goomez/CosmosX/Data/mission.json");

    public List<missionModel> listAll() throws Exception {
        return mapper.readValue(
                arquivo,
                new TypeReference<List<missionModel>>() {}
        );
    }

    public missionModel add(missionModel newMission) throws Exception{
        List<missionModel> mission = listAll();

        mission.add(newMission);

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(arquivo, mission);

        return newMission;
    }
}
