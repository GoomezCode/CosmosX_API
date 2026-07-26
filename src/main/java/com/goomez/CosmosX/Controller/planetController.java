package com.goomez.CosmosX.Controller;

import com.goomez.CosmosX.Model.planetModel;
import com.goomez.CosmosX.Service.planetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planet")
public class planetController {
    private final planetService service;

    public planetController(planetService service) {
        this.service = service;
    }

    @GetMapping
    public List<planetModel> listAll() throws Exception{
        return service.listAll();
    }

    @PostMapping
    public planetModel createAstronaut(@RequestBody planetModel planet) throws Exception{
        return service.add(planet);
    }
}
