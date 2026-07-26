package com.goomez.CosmosX.Controller;

import com.goomez.CosmosX.Model.astronautModel;
import com.goomez.CosmosX.Model.missionModel;
import com.goomez.CosmosX.Service.missionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mission")
public class missionController {
    private final missionService service;

    public missionController(missionService service) {
        this.service = service;
    }

    @GetMapping
    public List<missionModel> listAll() throws Exception{
        return service.listAll();
    }

    @PostMapping
    public missionModel createAstronaut(@RequestBody missionModel mission) throws Exception{
        return service.add(mission);
    }

}
