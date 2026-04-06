package com.battlepokemon.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.battlepokemon.app.model.Pokemon;
import com.battlepokemon.app.model.pokeEntity;
import com.battlepokemon.app.services.PokeConsultasService;
import com.battlepokemon.app.services.PokeService;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class PokeController {

    @Autowired
    private PokeService pokeService;

    @GetMapping("/battle/stream")
    public SseEmitter streamBatalla() {
        return pokeService.agregarCliente();
    }

    @Autowired
    private PokeConsultasService pokeConsultaService;

    @GetMapping("/api/pokemon/vida")
    public pokeEntity getVida() throws Exception {
        return pokeConsultaService.getVidaPokemons();
    }

    //Save pokemon
    @PostMapping("api/addpokemon")
    public Pokemon postMethodName(@RequestBody Pokemon pokemon) throws Exception{
        return pokeConsultaService.savePokemon(pokemon);
    }
    
}