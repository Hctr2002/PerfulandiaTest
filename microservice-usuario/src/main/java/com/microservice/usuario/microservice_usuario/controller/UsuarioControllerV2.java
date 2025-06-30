package com.microservice.usuario.microservice_usuario.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.microservice.usuario.microservice_usuario.assemblers.usuarioModelAssembler;
import com.microservice.usuario.microservice_usuario.dto.UsuarioDTO;
import com.microservice.usuario.microservice_usuario.model.Usuario;
import com.microservice.usuario.microservice_usuario.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/usuarios")
@Tag(name = "Usuarios V2", description = "Operaciones relacionadas con los usuarios")
public class UsuarioControllerV2 {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private usuarioModelAssembler usuarioAssembler;

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

    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @PostMapping
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en la base de datos")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioDTO dto) {
        try {
            Usuario usuario = new Usuario();
            usuario.setRut(dto.getRut());
            usuario.setNombres(dto.getNombres());
            usuario.setApellidos(dto.getApellidos());
            usuario.setCorreo(dto.getCorreo());

            Usuario creado = usuarioService.save(usuario);
            EntityModel<Usuario> usuarioModel = usuarioAssembler.toModel(creado);

            return ResponseEntity
                .created(usuarioModel.getRequiredLink("self").toUri())
                .body(usuarioModel);

        } catch (DataIntegrityViolationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "El correo o RUT ya está registrado");
            error.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }



    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(
            @PathVariable int id,
            @Valid @RequestBody UsuarioDTO dto) {

        Usuario existente = usuarioService.getUserById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        existente.setRut(dto.getRut());
        existente.setNombres(dto.getNombres());
        existente.setApellidos(dto.getApellidos());
        existente.setCorreo(dto.getCorreo());

        Usuario actualizado = usuarioService.save(existente);
        EntityModel<Usuario> usuarioModel = usuarioAssembler.toModel(actualizado);

        return ResponseEntity.ok(usuarioModel);
    }


    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por su ID")
    public ResponseEntity<?> eliminarUsuario(@PathVariable int id) {
        Usuario usuario = usuarioService.getUserById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        usuarioService.delete(id);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Usuario eliminado exitosamente");
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("links", List.of(
            linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withRel("usuarios").getHref()
        ));

        return ResponseEntity.ok(respuesta);
    }


}
