package com.goomez.CosmosX.Controller;

import com.goomez.CosmosX.Model.spacecraftModel;
import com.goomez.CosmosX.Service.spacecraftService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spacecraft")
public class spacecraftController {
    private final spacecraftService service;

    public spacecraftController(spacecraftService service) {
        this.service = service;
    }

    @GetMapping
    public List<spacecraftModel> listAll() throws Exception{
        return service.listAll();
    }

    @PostMapping
    public spacecraftModel createSpacecraft(@RequestBody spacecraftModel spacecraft) throws Exception{
        return service.add(spacecraft);
    }
}
