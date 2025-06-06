package com.microservice.venta.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {

    private int id_venta;
    private int id_usuario;
    private int nroBoleta;
    private String fechaCompra;
    private String producto;
    private int cantidad;
    private int total;

}
