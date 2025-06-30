package com.microservice.venta.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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
import org.springframework.web.server.ResponseStatusException;

import com.microservice.venta.assemblers.ventaModelAssembler;
import com.microservice.venta.dto.VentaDTO;
import com.microservice.venta.model.Venta;
import com.microservice.venta.service.VentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/ventas")
@Tag(name = "Ventas V2", description = "Operaciones relacionadas con las ventas")
public class VentaControllerV2 {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ventaModelAssembler ventaAssembler;

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Ventas no encontradas")
    })
    @Operation(summary = "Obtener ventas por id", description ="Obtiene la venta deseada por id")
    @GetMapping("/{id}")
    public EntityModel<Venta> getVentaById(@PathVariable int id) {
        Venta venta = ventaService.getVentaById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));
        return ventaAssembler.toModel(venta);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Ventas no encontradas")
    })
    @Operation(summary = "Obtener ventas por usuario", description ="Obtiene una lista de todas las ventas hechas por el usuario buscado")
    @GetMapping("/usuario/{id_usuario}")
    public CollectionModel<EntityModel<Venta>> getVentasByUsuario(@PathVariable int id_usuario) {
        List<EntityModel<Venta>> ventas = ventaService.getVentasByUsuarioId(id_usuario).stream()
            .map(ventaAssembler::toModel)
            .toList();

        return CollectionModel.of(ventas,
            linkTo(methodOn(VentaController.class).getVentasByUsuarioId(id_usuario)).withSelfRel());
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Ventas no encontradas")
    })
    @Operation(summary = "Obtener todas las ventas", description ="Obtiene una lista de todas las ventas")
    @GetMapping
    public CollectionModel<EntityModel<Venta>> getAllVentas() {
        List<EntityModel<Venta>> ventas = ventaService.findAll().stream()
            .map(ventaAssembler::toModel)
            .toList();

        return CollectionModel.of(ventas,
            linkTo(methodOn(VentaController.class).getAllUsers()).withSelfRel());
    }

    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @PostMapping
    @Operation(summary = "Crear nueva venta", description = "Crea una nueva venta en la base de datos")
    public ResponseEntity<?> crearVenta(@Valid @RequestBody VentaDTO dto) {
        try {
            Venta venta = new Venta();
            venta.setIdUsuario(dto.getId_usuario());
            venta.setNroBoleta(dto.getNroBoleta());
            venta.setFechaCompra(dto.getFechaCompra());
            venta.setProducto(dto.getProducto());
            venta.setCantidad(dto.getCantidad());
            venta.setTotal(dto.getTotal());

            Venta creada = ventaService.save(venta);
            EntityModel<Venta> ventaModel = ventaAssembler.toModel(creada);

            return ResponseEntity
                .created(ventaModel.getRequiredLink("self").toUri())
                .body(ventaModel);

        } catch (DataIntegrityViolationException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("mensaje", "El N° de boleta ya está registrado");
            error.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar venta", description = "Actualiza los datos de una venta existente")
    public ResponseEntity<EntityModel<Venta>> actualizarVenta(
            @PathVariable int id,
            @Valid @RequestBody VentaDTO dto) {

        Venta existente = ventaService.getVentaById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "venta no encontrada"));

        existente.setIdUsuario(dto.getId_usuario());
        existente.setNroBoleta(dto.getNroBoleta());
        existente.setFechaCompra(dto.getFechaCompra());
        existente.setProducto(dto.getProducto());
        existente.setCantidad(dto.getCantidad());
        existente.setTotal(dto.getTotal());

        Venta actualizada = ventaService.save(existente);
        EntityModel<Venta> ventaModel = ventaAssembler.toModel(actualizada);

        return ResponseEntity.ok(ventaModel);
    }

    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar venta", description = "Elimina una venta por su ID")
    public ResponseEntity<?> eliminarVenta(@PathVariable int id) {
        Venta venta = ventaService.getVentaById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        ventaService.delete(id);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Venta eliminada exitosamente");
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("links", List.of(
            linkTo(methodOn(VentaControllerV2.class).getAllVentas()).withRel("ventas").getHref()
        ));

        return ResponseEntity.ok(respuesta);
    }

}