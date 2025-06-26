package com.microservice.usuario.microservice_usuario;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.microservice.usuario.microservice_usuario.controller.UsuarioController;
import com.microservice.usuario.microservice_usuario.model.Usuario;
import com.microservice.usuario.microservice_usuario.service.UsuarioService;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void testGetAllUsers() throws Exception {
        List<Usuario> lista = List.of(
                Usuario.builder()
                        .id_usuario(1)
                        .rut("12345678-9")
                        .nombres("Héctor")
                        .apellidos("Robledo")
                        .correo("hector@mail.com")
                        .build()
        );

        when(usuarioService.findAll()).thenReturn(lista);

        mockMvc.perform(get("/api/v1/usuarios/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombres").value("Héctor"));
    }

    @Test
    void testGetUserById_found() throws Exception {
        Usuario usuario = Usuario.builder()
                .id_usuario(1)
                .rut("12345678-9")
                .nombres("Héctor")
                .apellidos("Robledo")
                .correo("hector@mail.com")
                .build();

        when(usuarioService.getUserById(1)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Héctor"))
                .andExpect(header().exists("mi-encabezado"));
    }

    @Test
    void testGetUserById_notFound() throws Exception {
        when(usuarioService.getUserById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No se encontró el usuario con ese ID: 99"))
                .andExpect(jsonPath("$.status").value("404"));
    }

    @Test
    void testSaveUser() throws Exception {
        Usuario usuario = Usuario.builder()
                .id_usuario(1)
                .rut("12345678-9")
                .nombres("Héctor")
                .apellidos("Robledo")
                .correo("hector@mail.com")
                .build();

        when(usuarioService.save(org.mockito.ArgumentMatchers.any(Usuario.class)))
                .thenReturn(usuario);

        String requestBody = """
            {
                "rut": "12345678-9",
                "nombres": "Héctor",
                "apellidos": "Robledo",
                "correo": "hector@mail.com"
            }
        """;

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$.correo").value("hector@mail.com"));
    }

    @Test
    void testSaveUser_conflict() throws Exception {
        when(usuarioService.save(any(Usuario.class)))
                .thenThrow(new DataIntegrityViolationException("Correo duplicado"));

        String requestBody = """
                {
                "rut": "12345678-9",
                "nombres": "Héctor",
                "apellidos": "Robledo",
                "correo": "hector@mail.com"
                }
        """;

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.messege").value("El email ya está registrado"));
    }


    @Test
    void testUpdateUser() throws Exception {
        Usuario usuarioActualizado = Usuario.builder()
                .id_usuario(1)
                .rut("12345678-9")
                .nombres("Héctor")
                .apellidos("Actualizado")
                .correo("hector@mail.com")
                .build();

        when(usuarioService.save(org.mockito.ArgumentMatchers.any(Usuario.class)))
                .thenReturn(usuarioActualizado);

        String requestBody = """
                {
                "rut": "12345678-9",
                "nombres": "Héctor",
                "apellidos": "Actualizado",
                "correo": "hector@mail.com"
                }
        """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellidos").value("Actualizado"));
    }

        @Test
        void testDeleteUser() throws Exception {
        doNothing().when(usuarioService).delete(1);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());
        }


    @Test
    void testUpdateUser_notFound() throws Exception {
        when(usuarioService.save(any(Usuario.class)))
                .thenThrow(new RuntimeException("No encontrado"));

        String requestBody = """
                {
                "rut": "12345678-9",
                "nombres": "Héctor",
                "apellidos": "Robledo",
                "correo": "hector@mail.com"
                }
        """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteUser_notFound() throws Exception {
        doThrow(new RuntimeException("No encontrado")).when(usuarioService).delete(1);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNotFound());
    }

}
