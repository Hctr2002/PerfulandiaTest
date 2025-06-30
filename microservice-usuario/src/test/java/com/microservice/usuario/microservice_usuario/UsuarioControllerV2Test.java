package com.microservice.usuario.microservice_usuario;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.usuario.microservice_usuario.assemblers.usuarioModelAssembler;
import com.microservice.usuario.microservice_usuario.controller.UsuarioControllerV2;
import com.microservice.usuario.microservice_usuario.dto.UsuarioDTO;
import com.microservice.usuario.microservice_usuario.model.Usuario;
import com.microservice.usuario.microservice_usuario.service.UsuarioService;

@WebMvcTest(UsuarioControllerV2.class)
public class UsuarioControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private usuarioModelAssembler usuarioAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetUsuarioPorId_200OK() throws Exception {
        Usuario usuario = new Usuario(1, "21150403-k", "Juan Enrique", "Perez Peña", "juan@mail.com");
        when(usuarioService.getUserById(1)).thenReturn(Optional.of(usuario));
        when(usuarioAssembler.toModel(usuario)).thenReturn(EntityModel.of(usuario));

        mockMvc.perform(get("/api/v2/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan Enrique"));
    }

    @Test
    void testGetUsuarioPorId_NotFound() throws Exception {
        when(usuarioService.getUserById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/usuarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsuarios_200OK() throws Exception {
        Usuario usuario1 = new Usuario(1, "21150403-k", "Juan Enrique", "Perez Peña", "juan@mail.com");
        Usuario usuario2 = new Usuario(2, "25236325-4", "Ana Maria", "Esmeralda Aguilar","ana@mail.com");

        when(usuarioService.findAll()).thenReturn(List.of(usuario1, usuario2));
        when(usuarioAssembler.toModel(usuario1)).thenReturn(EntityModel.of(usuario1));
        when(usuarioAssembler.toModel(usuario2)).thenReturn(EntityModel.of(usuario2));

        mockMvc.perform(get("/api/v2/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.usuarioList[0].id_usuario").value(1))
                .andExpect(jsonPath("_embedded.usuarioList[1].id_usuario").value(2));
    }

    @Test
    void testCrearUsuarioSuccess() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setRut("12345678-9");
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setCorreo("juan@example.com");

        Usuario creado = Usuario.builder()
            .id_usuario(1)
            .rut(dto.getRut())
            .nombres(dto.getNombres())
            .apellidos(dto.getApellidos())
            .correo(dto.getCorreo())
            .build();

        when(usuarioService.save(any(Usuario.class))).thenReturn(creado);
        when(usuarioAssembler.toModel(any(Usuario.class))).thenReturn(
            EntityModel.of(creado, linkTo(methodOn(UsuarioControllerV2.class).getUsuario(1)).withSelfRel())
        );

        mockMvc.perform(post("/api/v2/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id_usuario").value(1))
            .andExpect(jsonPath("$.nombres").value("Juan"))
            .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testActualizarUsuarioSuccess() throws Exception {
        int id = 1;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setRut("12345678-9");
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setCorreo("juan@example.com");

        Usuario existente = Usuario.builder()
            .id_usuario(id)
            .rut("11111111-1")
            .nombres("Antiguo")
            .apellidos("Apellido")
            .correo("viejo@mail.com")
            .build();

        Usuario actualizado = Usuario.builder()
            .id_usuario(id)
            .rut(dto.getRut())
            .nombres(dto.getNombres())
            .apellidos(dto.getApellidos())
            .correo(dto.getCorreo())
            .build();

        when(usuarioService.getUserById(id)).thenReturn(Optional.of(existente));
        when(usuarioService.save(any(Usuario.class))).thenReturn(actualizado);
        when(usuarioAssembler.toModel(any(Usuario.class))).thenReturn(
            EntityModel.of(actualizado, linkTo(methodOn(UsuarioControllerV2.class).getUsuario(id)).withSelfRel())
        );

        mockMvc.perform(put("/api/v2/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombres").value("Juan"))
            .andExpect(jsonPath("$.correo").value("juan@example.com"));
    }

    @Test
    void testEliminarUsuarioSuccess() throws Exception {
        int id = 1;
        Usuario usuario = Usuario.builder()
            .id_usuario(id)
            .rut("12345678-9")
            .nombres("Juan")
            .apellidos("Pérez")
            .correo("juan@example.com")
            .build();

        when(usuarioService.getUserById(id)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).delete(id);

        mockMvc.perform(delete("/api/v2/usuarios/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Usuario eliminado exitosamente"))
            .andExpect(jsonPath("$.links").exists());
    }

    @Test
    void testCrearUsuarioConflict() throws Exception {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setRut("12345678-9");
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setCorreo("duplicado@example.com");

        // Simula la excepción cuando se intenta guardar un duplicado
        when(usuarioService.save(any(Usuario.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));

        mockMvc.perform(post("/api/v2/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensaje").value("El correo o RUT ya está registrado"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testActualizarUsuarioNotFound() throws Exception {
        int id = 999;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setRut("12345678-9");
        dto.setNombres("Nuevo");
        dto.setApellidos("Nombre");
        dto.setCorreo("nuevo@correo.com");

        when(usuarioService.getUserById(id)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v2/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarUsuarioNotFound() throws Exception {
        int id = 999;

        when(usuarioService.getUserById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/v2/usuarios/{id}", id))
            .andExpect(status().isNotFound());
    }



}
