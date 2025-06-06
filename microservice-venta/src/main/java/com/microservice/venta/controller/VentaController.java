package com.microservice.venta.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.microservice.venta.dto.VentaDTO;
import com.microservice.venta.model.Venta;
import com.microservice.venta.service.VentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    //localhost:9090/api/v1/ventas/listar
    @GetMapping("/listar")
    public List<Venta> getAllUsers() {
        return ventaService.findAll();
    }

    //localhost:9090/api/v1/ventas/{id_venta}
    @GetMapping("/{id_venta}")
    public ResponseEntity<?> getVentaById(@PathVariable Integer id_venta) {
        
        Optional<Venta> venta = ventaService.getVentaById(id_venta);

        if(venta.isPresent()){
            
            VentaDTO dto = new VentaDTO();
            dto.setId_venta(venta.get().getId_venta());
            dto.setId_usuario(venta.get().getId_usuario());
            dto.setNroBoleta(venta.get().getNroBoleta());
            dto.setFechaCompra(venta.get().getFechaCompra());
            dto.setProducto(venta.get().getProducto());
            dto.setCantidad(venta.get().getCantidad());
            dto.setTotal(venta.get().getTotal());


            return ResponseEntity.ok()
                        .header("mi-encabezado","valor")
                        .body(venta.get());

        }else{
            Map<String,String> errorBody = new HashMap<>();
            errorBody.put("message","No se encontró la venta con ese ID: " + id_venta);
            errorBody.put("status","404");
            errorBody.put("timestamp",LocalDateTime.now().toString());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorBody);
        }

    }


    @PostMapping
        public ResponseEntity<?> save(@Valid @RequestBody VentaDTO ventaDTO) {
        try{

            Venta venta = new Venta();
            venta.setId_usuario(ventaDTO.getId_usuario());
            venta.setNroBoleta(ventaDTO.getNroBoleta());
            venta.setFechaCompra(ventaDTO.getFechaCompra());
            venta.setProducto(ventaDTO.getProducto());
            venta.setCantidad(ventaDTO.getCantidad());
            venta.setTotal(ventaDTO.getTotal());

            Venta ventaGuardada = ventaService.save(venta);

            VentaDTO responseDTO = new VentaDTO();
            responseDTO.setId_venta(ventaGuardada.getId_venta());
            responseDTO.setId_usuario(ventaGuardada.getId_usuario());
            responseDTO.setNroBoleta(ventaGuardada.getNroBoleta());
            responseDTO.setFechaCompra(ventaGuardada.getFechaCompra());
            responseDTO.setProducto(ventaGuardada.getProducto());
            responseDTO.setCantidad(ventaGuardada.getCantidad());
            responseDTO.setTotal(ventaGuardada.getTotal());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(ventaGuardada.getId_venta())
                    .toUri();

            return ResponseEntity.created(location).body(responseDTO);

        } catch (DataIntegrityViolationException e) {
            // Da error si hay un campo único duplicado
            Map<String, String> error = new HashMap<>();
            error.put("message", "El número de boleta ya está registrado");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }



    //localhost:9090/api/v1/ventas/{id_venta}
    @PutMapping("{id_venta}")
    public ResponseEntity<VentaDTO> update(@PathVariable int id_venta, @RequestBody VentaDTO ventaDTO) {
        try {
            
            Venta venta = new Venta();
            venta.setId_venta(id_venta);
            venta.setId_usuario(ventaDTO.getId_usuario());
            venta.setNroBoleta(ventaDTO.getNroBoleta());
            venta.setFechaCompra(ventaDTO.getFechaCompra());
            venta.setProducto(ventaDTO.getProducto());
            venta.setCantidad(ventaDTO.getCantidad());
            venta.setTotal(ventaDTO.getTotal());

            Venta ventaActualizada = ventaService.save(venta);
        
            VentaDTO responseDTO = new VentaDTO();
            responseDTO.setId_venta(ventaActualizada.getId_venta());
            responseDTO.setId_usuario(ventaActualizada.getId_usuario());
            responseDTO.setNroBoleta(ventaActualizada.getNroBoleta());
            responseDTO.setFechaCompra(ventaActualizada.getFechaCompra());
            responseDTO.setProducto(ventaActualizada.getProducto());
            responseDTO.setCantidad(ventaActualizada.getCantidad());
            responseDTO.setTotal(ventaActualizada.getTotal());
            
            return ResponseEntity.ok(responseDTO);

        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }



    //localhost:9090/api/v1/ventas/{id_venta}
    @DeleteMapping("/{id_venta}")
    public ResponseEntity<?> eliminar(@PathVariable int id_venta){
        try{

            ventaService.delete(id_venta);
            return ResponseEntity.noContent().build();

        }catch(Exception ex){
            return ResponseEntity.notFound().build();
        }
    }
}
