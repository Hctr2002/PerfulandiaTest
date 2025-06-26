package com.microservice.usuario.microservice_usuario;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.usuario.microservice_usuario.model.Usuario;
import com.microservice.usuario.microservice_usuario.repository.UsuarioRepository;
import com.microservice.usuario.microservice_usuario.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllUsuarios() {
        List<Usuario> listaMock = List.of(
            Usuario.builder()
                    .id_usuario(1)
                    .rut("12345678-9")
                    .nombres("Héctor")
                    .apellidos("Robledo")
                    .correo("hector@mail.com")
                    .build(),
            Usuario.builder()
                    .id_usuario(2)
                    .rut("98765432-1")
                    .nombres("Laura")
                    .apellidos("Gómez")
                    .correo("laura@mail.com")
                    .build()
        );

        when(usuarioRepository.findAll()).thenReturn(listaMock);

        List<Usuario> resultado = usuarioService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Héctor", resultado.get(0).getNombres());
        assertEquals("Laura", resultado.get(1).getNombres());
    }

    @Test
    void testGetUserById() {
        Usuario usuario = Usuario.builder()
                .id_usuario(1)
                .rut("12345678-9")
                .nombres("Héctor")
                .apellidos("Robledo")
                .correo("hector@mail.com")
                .build();

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.getUserById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Héctor", resultado.get().getNombres());
    }
    
    @Test
    void testGetUserById2() {
        Usuario usuario = Usuario.builder()
                .id_usuario(2)
                .rut("87654321-0")
                .nombres("Carlos")
                .apellidos("Pérez")
                .correo("carlos@mail.com")
                .build();

        when(usuarioRepository.findById(2)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.getUserById2(2);

        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getNombres());
    }

    @Test
    void testSaveUsuario() {
        Usuario usuario = Usuario.builder()
                .rut("11222333-4")
                .nombres("Ana")
                .apellidos("López")
                .correo("ana@mail.com")
                .build();

        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.save(usuario);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNombres());
    }

    @Test
    void testDeleteUsuario() {
        int id = 10;

        usuarioService.delete(id);

        // Solo verifica que se llama al método; no lanza excepción
        // (alternativamente puedes usar verify si quieres test estricto)
        assertDoesNotThrow(() -> usuarioService.delete(id));
    }
}
