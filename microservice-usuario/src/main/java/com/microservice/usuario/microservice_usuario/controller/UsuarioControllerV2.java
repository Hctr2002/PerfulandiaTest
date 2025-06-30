package com.microservice.usuario.microservice_usuario.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.usuario.microservice_usuario.assemblers.usuarioModelAssembler;

import com.microservice.usuario.microservice_usuario.model.Usuario;
import com.microservice.usuario.microservice_usuario.service.UsuarioService;


import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v2/usuarios")
public class UsuarioControllerV2 {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private usuarioModelAssembler usuarioAssembler;

    // Obtener usuario por ID con HATEOAS
    @GetMapping("/{id}")
    public EntityModel<Usuario> getUsuario(@PathVariable int id) {
        Usuario usuario = usuarioService.getUserById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return usuarioAssembler.toModel(usuario);
    }

    // Obtener todos los usuarios con HATEOAS
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> getAllUsuarios() {
        List<EntityModel<Usuario>> usuarios = usuarioService.findAll().stream()
            .map(usuarioAssembler::toModel)
            .toList();

        return CollectionModel.of(usuarios,
            linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withSelfRel());
    }

    @GetMapping("/test")
    public String testV2() {
        return "Usuario V2 funciona";
    }


}
