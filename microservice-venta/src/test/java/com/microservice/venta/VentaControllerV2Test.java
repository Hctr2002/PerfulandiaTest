package com.microservice.venta;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.microservice.venta.assemblers.ventaModelAssembler;
import com.microservice.venta.controller.VentaControllerV2;
import com.microservice.venta.model.Venta;
import com.microservice.venta.service.VentaService;

@WebMvcTest(VentaControllerV2.class)
public class VentaControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @MockBean
    private ventaModelAssembler ventaAssembler;

    private Venta venta;

    @BeforeEach
    void setUp() {
        venta = new Venta();
        venta.setId_venta(1);
        venta.setIdUsuario(100);
    }

    @Test
    void testGetVentaById_found() throws Exception {
        when(ventaService.getVentaById(1)).thenReturn(Optional.of(venta));
        when(ventaAssembler.toModel(venta)).thenReturn(EntityModel.of(venta));

        mockMvc.perform(get("/api/v2/ventas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_venta").value(1));
    }

    @Test
    void testGetVentaById_notFound() throws Exception {
        when(ventaService.getVentaById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/ventas/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetVentasByUsuario() throws Exception {
        when(ventaService.getVentasByUsuarioId(100)).thenReturn(Arrays.asList(venta));
        when(ventaAssembler.toModel(venta)).thenReturn(EntityModel.of(venta));

        mockMvc.perform(get("/api/v2/ventas/usuario/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_embedded").exists());
    }

    @Test
    void testGetAllVentas() throws Exception {
        when(ventaService.findAll()).thenReturn(Arrays.asList(venta));
        when(ventaAssembler.toModel(venta)).thenReturn(EntityModel.of(venta));

        mockMvc.perform(get("/api/v2/ventas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_embedded").exists());
    }
}