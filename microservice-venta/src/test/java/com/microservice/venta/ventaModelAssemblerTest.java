package com.microservice.venta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import com.microservice.venta.assemblers.ventaModelAssembler;
import com.microservice.venta.model.Venta;

class ventaModelAssemblerTest {

    private ventaModelAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new ventaModelAssembler();
    }

    @Test
    void testToModel_AddsExpectedLinks() {
        // Arrange
        Venta venta = new Venta();
        venta.setId_venta(1);
        venta.setIdUsuario(100);

        // Act
        EntityModel<Venta> model = assembler.toModel(venta);

        // Assert
        assertNotNull(model);
        assertEquals(venta, model.getContent());

        Link selfLink = model.getLink("self").orElse(null);
        Link usuarioLink = model.getLink("usuario").orElse(null);
        Link ventasDelUsuarioLink = model.getLink("ventas-del-usuario").orElse(null);
        Link todasLasVentasLink = model.getLink("todas-las-ventas").orElse(null);

        assertNotNull(selfLink, "Debe contener link 'self'");
        assertTrue(selfLink.getHref().contains("/api/v2/ventas/1"));

        assertNotNull(usuarioLink, "Debe contener link 'usuario'");
        assertEquals("http://localhost:8080/api/v2/usuarios/100", usuarioLink.getHref());

        assertNotNull(ventasDelUsuarioLink, "Debe contener link 'ventas-del-usuario'");
        assertTrue(ventasDelUsuarioLink.getHref().contains("/api/v2/ventas/usuario/100"));

        assertNotNull(todasLasVentasLink, "Debe contener link 'todas-las-ventas'");
        assertTrue(todasLasVentasLink.getHref().contains("/api/v2/ventas"));
    }
}
