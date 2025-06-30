package com.microservice.usuario.microservice_usuario;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import com.microservice.usuario.microservice_usuario.assemblers.usuarioModelAssembler;
import com.microservice.usuario.microservice_usuario.model.Usuario;

public class UsuarioModelAssemblerTest {

    private final usuarioModelAssembler assembler = new usuarioModelAssembler();

    @Test
    void toModel_agregaTodosLosLinksCorrectos() {
        // Arrange
        Usuario usuario = new Usuario(1, "21150403-k", "Juan Enrique", "Perez Peña", "juan@mail.com");

        // Act
        EntityModel<Usuario> model = assembler.toModel(usuario);

        // Assert
        assertThat(model.getContent()).isEqualTo(usuario);

        assertThat(model.getLinks("self"))
            .hasSize(1)
            .first()
            .extracting(link -> link.getHref())
            .asString()
            .contains("/api/v2/usuarios/1");

        assertThat(model.getLinks("usuarios"))
            .hasSize(1)
            .first()
            .extracting(link -> link.getHref())
            .asString()
            .contains("/api/v2/usuarios");

        assertThat(model.getLinks("ventas"))
            .hasSize(1)
            .first()
            .extracting(link -> link.getHref())
            .asString()
            .isEqualTo("http://localhost:8080/api/v2/ventas/usuario/1");
    }
}