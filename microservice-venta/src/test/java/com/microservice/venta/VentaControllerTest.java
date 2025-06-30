package com.microservice.venta;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.venta.controller.VentaController;
import com.microservice.venta.dto.VentaDTO;
import com.microservice.venta.model.Venta;
import com.microservice.venta.service.VentaService;

@WebMvcTest(VentaController.class)
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllUsers() throws Exception {
        Venta v1 = Venta.builder().id_venta(1).producto("Prod1").build();
        Venta v2 = Venta.builder().id_venta(2).producto("Prod2").build();

        when(ventaService.findAll()).thenReturn(Arrays.asList(v1, v2));

        mockMvc.perform(get("/api/v1/ventas/listar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].producto").value("Prod1"))
            .andExpect(jsonPath("$[1].producto").value("Prod2"));

        verify(ventaService, times(1)).findAll();
    }

    @Test
    void testGetVentaById_Existente() throws Exception {
        Venta venta = Venta.builder()
                .id_venta(1)
                .idUsuario(100)
                .nroBoleta(123)
                .fechaCompra("2025-06-26")
                .producto("Producto Test")
                .cantidad(2)
                .total(5000)
                .build();

        when(ventaService.getVentaById(1)).thenReturn(Optional.of(venta));

        mockMvc.perform(get("/api/v1/ventas/1"))
            .andExpect(status().isOk())
            .andExpect(header().string("mi-encabezado", "valor"))
            .andExpect(jsonPath("$.producto").value("Producto Test"));

        verify(ventaService, times(1)).getVentaById(1);
    }

    @Test
    void testGetVentaById_NoExistente() throws Exception {
        when(ventaService.getVentaById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/ventas/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("No se encontró la venta con ese ID: 1"))
            .andExpect(jsonPath("$.status").value("404"))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(ventaService, times(1)).getVentaById(1);
    }

    @Test
    void testSaveVenta_Exitosa() throws Exception {
        VentaDTO dto = new VentaDTO();
        dto.setId_usuario(100);
        dto.setNroBoleta(123);
        dto.setFechaCompra("2025-06-26");
        dto.setProducto("Producto Save");
        dto.setCantidad(3);
        dto.setTotal(7500);

        Venta ventaGuardada = Venta.builder()
                .id_venta(1)
                .idUsuario(dto.getId_usuario())
                .nroBoleta(dto.getNroBoleta())
                .fechaCompra(dto.getFechaCompra())
                .producto(dto.getProducto())
                .cantidad(dto.getCantidad())
                .total(dto.getTotal())
                .build();

        when(ventaService.save(any(Venta.class))).thenReturn(ventaGuardada);

        mockMvc.perform(post("/api/v1/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id_venta").value(1))
            .andExpect(jsonPath("$.producto").value("Producto Save"));

        verify(ventaService, times(1)).save(any(Venta.class));
    }

    @Test
    void testSaveVenta_ConflictoNumeroBoleta() throws Exception {
        VentaDTO dto = new VentaDTO();
        dto.setId_usuario(100);
        dto.setNroBoleta(123);
        dto.setFechaCompra("2025-06-26");
        dto.setProducto("Producto Save");
        dto.setCantidad(3);
        dto.setTotal(7500);

        when(ventaService.save(any(Venta.class)))
            .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicado"));

        mockMvc.perform(post("/api/v1/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("El número de boleta ya está registrado"));

        verify(ventaService, times(1)).save(any(Venta.class));
    }

    @Test
    void testUpdateVenta_Exitosa() throws Exception {
        VentaDTO dto = new VentaDTO();
        dto.setId_usuario(100);
        dto.setNroBoleta(123);
        dto.setFechaCompra("2025-06-26");
        dto.setProducto("Producto Update");
        dto.setCantidad(5);
        dto.setTotal(12500);

        Venta ventaActualizada = Venta.builder()
                .id_venta(1)
                .idUsuario(dto.getId_usuario())
                .nroBoleta(dto.getNroBoleta())
                .fechaCompra(dto.getFechaCompra())
                .producto(dto.getProducto())
                .cantidad(dto.getCantidad())
                .total(dto.getTotal())
                .build();

        when(ventaService.save(any(Venta.class))).thenReturn(ventaActualizada);

        mockMvc.perform(put("/api/v1/ventas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_venta").value(1))
            .andExpect(jsonPath("$.producto").value("Producto Update"));

        verify(ventaService, times(1)).save(any(Venta.class));
    }

    @Test
    void testUpdateVenta_Error() throws Exception {
        VentaDTO dto = new VentaDTO();
        dto.setId_usuario(100);
        dto.setNroBoleta(123);
        dto.setFechaCompra("2025-06-26");
        dto.setProducto("Producto Update");
        dto.setCantidad(5);
        dto.setTotal(12500);

        when(ventaService.save(any(Venta.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/v1/ventas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isNotFound());

        verify(ventaService, times(1)).save(any(Venta.class));
    }

    @Test
    void testEliminarVenta_Exitosa() throws Exception {
        doNothing().when(ventaService).delete(1);

        mockMvc.perform(delete("/api/v1/ventas/1"))
            .andExpect(status().isNoContent());

        verify(ventaService, times(1)).delete(1);
    }

    @Test
    void testEliminarVenta_Error() throws Exception {
        doThrow(new RuntimeException("No encontrado")).when(ventaService).delete(1);

        mockMvc.perform(delete("/api/v1/ventas/1"))
            .andExpect(status().isNotFound());

        verify(ventaService, times(1)).delete(1);
    }

}
