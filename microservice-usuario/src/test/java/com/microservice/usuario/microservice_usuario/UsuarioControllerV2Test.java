package com.microservice.usuario.microservice_usuario;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.microservice.usuario.microservice_usuario.assemblers.usuarioModelAssembler;
import com.microservice.usuario.microservice_usuario.controller.UsuarioControllerV2;
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
}
