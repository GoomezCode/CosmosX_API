package com.goomez.CosmosX.Controller;

import com.goomez.CosmosX.Model.astronautModel;
import com.goomez.CosmosX.Service.astronautService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/astronauts")
public class astronautController {
    private final astronautService service;

    public astronautController(astronautService service) {
        this.service = service;
    }

    @GetMapping
    public List<astronautModel> listAll() throws Exception{
        return service.listAll();
    }

    @GetMapping("/{id}")
    public astronautModel listId(@PathVariable Long id) throws Exception{
        return service.listId(id);
    }

    @PostMapping
    public astronautModel createAstronaut(@RequestBody astronautModel astronaut) throws Exception{
        return service.add(astronaut);
    }

    @DeleteMapping("/{id}")
    public String deleteAstronaut(@PathVariable Long id) throws Exception{
        boolean removido = service.delete(id);

        if (removido){
            return "Astronaut successfully removed!";
        }else{return "Astronaut not found!";}
    }

}
