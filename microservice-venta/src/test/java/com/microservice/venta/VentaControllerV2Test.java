package com.microservice.venta;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
import com.microservice.venta.assemblers.ventaModelAssembler;
import com.microservice.venta.controller.VentaControllerV2;
import com.microservice.venta.dto.VentaDTO;
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
    
    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void testCrearVentaSuccess() throws Exception {
        VentaDTO dto = new VentaDTO();
        dto.setId_usuario(1);
        dto.setNroBoleta(56985);
        dto.setFechaCompra("25/12/2025");
        dto.setProducto("PerfumeTest");
        dto.setCantidad(5);
        dto.setTotal(99999);

        Venta creada = Venta.builder()
            .id_venta(1)
            .idUsuario(dto.getId_usuario())
            .nroBoleta(dto.getNroBoleta())
            .fechaCompra(dto.getFechaCompra())
            .producto(dto.getProducto())
            .cantidad(dto.getCantidad())
            .total(dto.getTotal())
            .build();

        when(ventaService.save(any(Venta.class))).thenReturn(creada);
        when(ventaAssembler.toModel(any(Venta.class))).thenReturn(
            EntityModel.of(creada, linkTo(methodOn(VentaControllerV2.class).getVentaById(1)).withSelfRel())
        );

        mockMvc.perform(post("/api/v2/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id_venta").value(1))
            .andExpect(jsonPath("$.idUsuario").value(1))
            .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void testActualizarVentaSuccess() throws Exception {
        int id = 1;
        VentaDTO dto = new VentaDTO();
        dto.setId_usuario(1);
        dto.setNroBoleta(56985);
        dto.setFechaCompra("25/12/2025");
        dto.setProducto("PerfumeTest");
        dto.setCantidad(5);
        dto.setTotal(99999);

        Venta existente = Venta.builder()
            .id_venta(id)
            .idUsuario(dto.getId_usuario())
            .nroBoleta(dto.getNroBoleta())
            .fechaCompra(dto.getFechaCompra())
            .producto(dto.getProducto())
            .cantidad(dto.getCantidad())
            .total(dto.getTotal())
            .build();

        Venta actualizada = Venta.builder()
            .id_venta(id)
            .idUsuario(dto.getId_usuario())
            .nroBoleta(dto.getNroBoleta())
            .fechaCompra(dto.getFechaCompra())
            .producto(dto.getProducto())
            .cantidad(dto.getCantidad())
            .total(dto.getTotal())
            .build();

        when(ventaService.getVentaById(id)).thenReturn(Optional.of(existente));
        when(ventaService.save(any(Venta.class))).thenReturn(actualizada);
        when(ventaAssembler.toModel(any(Venta.class))).thenReturn(
            EntityModel.of(actualizada, linkTo(methodOn(VentaControllerV2.class).getVentaById(id)).withSelfRel())
        );

        mockMvc.perform(put("/api/v2/ventas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_venta").value(id))
            .andExpect(jsonPath("$.idUsuario").value(1));
    }

    @Test
    void testEliminarVentaSuccess() throws Exception {
        int id = 1;
        Venta venta = Venta.builder()
            .id_venta(id)
            .idUsuario(1)
            .nroBoleta(56565)
            .fechaCompra("25/12/2025")
            .producto("PerfumeTest")
            .cantidad(5)
            .total(999999)
            .build();

        when(ventaService.getVentaById(id)).thenReturn(Optional.of(venta));
        doNothing().when(ventaService).delete(id);

        mockMvc.perform(delete("/api/v2/ventas/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mensaje").value("Venta eliminada exitosamente"))
            .andExpect(jsonPath("$.links").exists());
    }

    @Test
    void testCrearVentaConflict() throws Exception {
        VentaDTO dto = new VentaDTO();
        dto.setId_usuario(1);
        dto.setNroBoleta(56985);
        dto.setFechaCompra("25/12/2025");
        dto.setProducto("PerfumeTest");
        dto.setCantidad(5);
        dto.setTotal(99999);

        // Simula la excepción cuando se intenta guardar un duplicado
        when(ventaService.save(any(Venta.class))).thenThrow(new DataIntegrityViolationException("Duplicate"));

        mockMvc.perform(post("/api/v2/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensaje").value("El Nro de Boleta ya está registrado"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testActualizarVentaNotFound() throws Exception {
        int id = 999;
        VentaDTO dto = new VentaDTO();
        dto.setId_usuario(1);
        dto.setNroBoleta(56985);
        dto.setFechaCompra("25/12/2025");
        dto.setProducto("PerfumeTest");
        dto.setCantidad(5);
        dto.setTotal(99999);

        when(ventaService.getVentaById(id)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v2/ventas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarVentaNotFound() throws Exception {
        int id = 999;

        when(ventaService.getVentaById(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/v2/ventas/{id}", id))
            .andExpect(status().isNotFound());
    }

}