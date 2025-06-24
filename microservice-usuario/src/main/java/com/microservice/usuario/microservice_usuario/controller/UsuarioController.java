package com.microservice.usuario.microservice_usuario.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.microservice.usuario.microservice_usuario.dto.UsuarioDTO;
import com.microservice.usuario.microservice_usuario.model.Usuario;
import com.microservice.usuario.microservice_usuario.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    //localhost:8090/api/v1/usuarios/listar
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Usuarios no encontrados")
    })
    @Operation(summary = "Obtener todos los usuarios", description ="Obtiene una lista de todos los usuarios")
    @GetMapping("/listar")
    public List<Usuario> getAllUsers() {
        return usuarioService.findAll();
    }
    
    //localhost:8090/api/v1/usuarios/{id_usuario}
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @Operation(summary = "Obtener usuario por codigo", description ="Obtiene al usuario deseado por codigo")
    @GetMapping("/{id_usuario}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id_usuario) {
        
        Optional<Usuario> usuario = usuarioService.getUserById(id_usuario);

        if(usuario.isPresent()){
            
            UsuarioDTO dto = new UsuarioDTO();
            dto.setId_usuario(usuario.get().getId_usuario());
            dto.setRut(usuario.get().getRut());
            dto.setNombres(usuario.get().getNombres());
            dto.setApellidos(usuario.get().getApellidos());
            dto.setCorreo(usuario.get().getCorreo());

            return ResponseEntity.ok()
                        .header("mi-encabezado","valor")
                        .body(usuario.get());

        }else{
            Map<String,String> errorBody = new HashMap<>();
            errorBody.put("message","No se encontró el usuario con ese ID: " + id_usuario);
            errorBody.put("status","404");
            errorBody.put("timestamp",LocalDateTime.now().toString());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorBody);
        }

    }

    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @Operation(summary = "Crear usuario", description ="ingresa un usuario nuevo")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try{

            Usuario usuario = new Usuario();
            usuario.setRut(usuarioDTO.getRut());
            usuario.setNombres(usuarioDTO.getNombres());
            usuario.setApellidos(usuarioDTO.getApellidos());
            usuario.setCorreo(usuarioDTO.getCorreo());

            Usuario usuarioGuardado = usuarioService.save(usuario);

            UsuarioDTO responseDTO = new UsuarioDTO();
            responseDTO.setId_usuario(usuarioGuardado.getId_usuario());
            responseDTO.setRut(usuarioGuardado.getRut());
            responseDTO.setNombres(usuarioGuardado.getNombres());
            responseDTO.setApellidos(usuarioGuardado.getApellidos());
            responseDTO.setCorreo(usuarioGuardado.getCorreo());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(usuarioGuardado.getId_usuario())
                    .toUri();

            return ResponseEntity.created(location).body(responseDTO);

        }catch(DataIntegrityViolationException e){
            //Da error si hay un campo único duplicado
            Map<String,String> error = new HashMap<>();
            error.put("messege","El email ya está registrado");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }
    

    //localhost:8090/api/v1/usuarios/{id_usuario}
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @Operation(summary = "Actualizar usuario", description ="Actualiza un usuario existente")
    @PutMapping("{id_usuario}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable int id_usuario, @RequestBody UsuarioDTO usuarioDTO) {
        try {
            
            Usuario usuario = new Usuario();
            usuario.setId_usuario(id_usuario);
            usuario.setRut(usuarioDTO.getRut());
            usuario.setNombres(usuarioDTO.getNombres());
            usuario.setApellidos(usuarioDTO.getApellidos());
            usuario.setCorreo(usuarioDTO.getCorreo());

            Usuario usuarioActualizado = usuarioService.save(usuario);
        
            UsuarioDTO responseDTO = new UsuarioDTO();
            responseDTO.setId_usuario(usuarioActualizado.getId_usuario());
            responseDTO.setRut(usuarioActualizado.getRut());
            responseDTO.setNombres(usuarioActualizado.getNombres());
            responseDTO.setApellidos(usuarioActualizado.getApellidos());
            responseDTO.setCorreo(usuarioActualizado.getCorreo());
            
            return ResponseEntity.ok(responseDTO);


        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    //localhost:8090/api/v1/usuarios/{id_usuario}
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrada")
    })
    @Operation(summary = "Eliminar usuario", description ="Elimina un usuario")
    @DeleteMapping("/{id_usuario}")
    public ResponseEntity<?> eliminar(@PathVariable int id_usuario){
        try{

            usuarioService.delete(id_usuario);
            return ResponseEntity.noContent().build();

        }catch(Exception ex){
            return ResponseEntity.notFound().build();
        }
    }
    
    

}
