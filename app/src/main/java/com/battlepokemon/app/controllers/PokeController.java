package com.battlepokemon.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.battlepokemon.app.model.pokeEntity;
import com.battlepokemon.app.services.PokeConsultaService;
import com.battlepokemon.app.services.PokeService;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class PokeController {

    @Autowired
    private PokeService pokeService;

    @GetMapping("/battle/stream")
    public SseEmitter streamBatalla() {
        return pokeService.agregarCliente();
    }

    @Autowired
    private PokeConsultaService pokeConsultaService;

    @GetMapping("/api/pokemon/vida")
    public pokeEntity getVida() throws Exception {
        return pokeConsultaService.getVidaPokemons();
    }
}