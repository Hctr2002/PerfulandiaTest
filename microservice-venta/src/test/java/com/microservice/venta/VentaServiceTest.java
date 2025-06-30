package com.microservice.venta;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.venta.model.Venta;
import com.microservice.venta.repository.VentaRepository;
import com.microservice.venta.service.VentaService;

@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private VentaService ventaService;

    @Test
    void testObtenerVentaPorIdExistente() {
        Venta venta = Venta.builder()
                .id_venta(1)
                .idUsuario(100)
                .nroBoleta(1234)
                .fechaCompra("2025-06-26")
                .producto("Producto A")
                .cantidad(2)
                .total(2000)
                .build();

        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));

        Optional<Venta> resultado = ventaService.getVentaById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Producto A", resultado.get().getProducto());

        verify(ventaRepository, times(1)).findById(1);
    }

    @Test
    void testGuardarVenta() {
        Venta venta = Venta.builder()
                .idUsuario(100)
                .nroBoleta(1235)
                .fechaCompra("2025-06-26")
                .producto("Producto B")
                .cantidad(3)
                .total(3000)
                .build();

        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.save(venta);

        assertNotNull(resultado);
        assertEquals("Producto B", resultado.getProducto());

        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    void testFindAll() {
        Venta venta1 = Venta.builder().id_venta(1).producto("Producto A").build();
        Venta venta2 = Venta.builder().id_venta(2).producto("Producto B").build();

        List<Venta> listaVentas = Arrays.asList(venta1, venta2);

        when(ventaRepository.findAll()).thenReturn(listaVentas);

        List<Venta> resultado = ventaService.findAll();

        assertEquals(2, resultado.size());
        assertEquals("Producto A", resultado.get(0).getProducto());
        assertEquals("Producto B", resultado.get(1).getProducto());

        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void testGetVentaById2_Existente() {
        Venta venta = Venta.builder()
                .id_venta(1)
                .producto("Producto A")
                .build();

        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.getVentaById2(1);

        assertNotNull(resultado);
        assertEquals("Producto A", resultado.getProducto());

        verify(ventaRepository, times(1)).findById(1);
    }

    @Test
    void testGetVentaById2_NoExistente() {
        when(ventaRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(java.util.NoSuchElementException.class, () -> {
            ventaService.getVentaById2(1);
        });

        verify(ventaRepository, times(1)).findById(1);
    }

    @Test
    void testDelete() {
        int id = 1;

        doNothing().when(ventaRepository).deleteById(id);

        ventaService.delete(id);

        verify(ventaRepository, times(1)).deleteById(id);
    }

    @Test
    void testGetVentasByUsuarioId() {
        Venta venta = new Venta(); venta.setId_venta(1); venta.setIdUsuario(10);
        when(ventaRepository.findByIdUsuario(10)).thenReturn(Arrays.asList(venta));

        List<Venta> result = ventaService.getVentasByUsuarioId(10);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getIdUsuario());
        verify(ventaRepository).findByIdUsuario(10);
    }

}
