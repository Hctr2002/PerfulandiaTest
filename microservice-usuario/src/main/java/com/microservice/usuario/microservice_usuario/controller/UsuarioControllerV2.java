package com.microservice.usuario.microservice_usuario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.microservice.usuario.microservice_usuario.assemblers.usuarioModelAssembler;
import com.microservice.usuario.microservice_usuario.model.Usuario;
import com.microservice.usuario.microservice_usuario.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2/usuarios")
@Tag(name = "Usuarios V2", description = "Operaciones relacionadas con los usuarios")
public class UsuarioControllerV2 {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private usuarioModelAssembler usuarioAssembler;

    // Obtener usuario por ID con HATEOAS
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Usuarios no encontrados")
    })
    @Operation(summary = "Obtener usuario por id", description ="Obtiene el usuario deseado por su id")
    @GetMapping("/{id}")
    public EntityModel<Usuario> getUsuario(@PathVariable int id) {
        Usuario usuario = usuarioService.getUserById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return usuarioAssembler.toModel(usuario);
    }

    // Obtener todos los usuarios con HATEOAS
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Usuarios no encontrados")
    })
    @Operation(summary = "Obtener todos los usuarios", description ="Obtiene una lista de todos los usuarios")
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> getAllUsuarios() {
        List<EntityModel<Usuario>> usuarios = usuarioService.findAll().stream()
            .map(usuarioAssembler::toModel)
            .toList();

        return CollectionModel.of(usuarios,
            linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withSelfRel());
    }


}
