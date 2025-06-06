package com.microservice.venta.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservice.venta.model.Venta;
import com.microservice.venta.repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    public List<Venta> findAll(){
        return ventaRepository.findAll();
    }

    public Optional<Venta> getVentaById(int id_venta){
        return ventaRepository.findById(id_venta);
    }

    public Venta getVentaById2(int id){
        return ventaRepository.findById(id).get();
    }

    public Venta save(Venta venta){
        return ventaRepository.save(venta);
    }

    public void delete(int id_venta){
        ventaRepository.deleteById(id_venta);
    }

}
