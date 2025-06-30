package com.microservice.venta.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id_venta;

    @Column(nullable=false)
    private int idUsuario;

    @Column(nullable=false, unique = true)
    private int nroBoleta;

    @Column(name = "fecha_compra", nullable = false)
    private String fechaCompra;

    @Column(nullable=false)
    @Size(min=2, max=100, message="El nombre deben tener entre 2 y 100 caracteres")
    private String producto;

    @Column(nullable=false)
    private int cantidad;

    @Column(nullable=false)
    private int total;

}
