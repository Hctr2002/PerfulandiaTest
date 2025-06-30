package com.microservice.venta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.venta.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta,Integer>{

    List<Venta> findByIdUsuario(Integer idUsuario);

}
