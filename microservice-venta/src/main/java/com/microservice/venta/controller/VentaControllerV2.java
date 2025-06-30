package com.microservice.venta.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.venta.assemblers.ventaModelAssembler;
import com.microservice.venta.model.Venta;
import com.microservice.venta.service.VentaService;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v2/ventas")
public class VentaControllerV2 {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ventaModelAssembler ventaAssembler;

    @GetMapping("/{id}")
    public EntityModel<Venta> getVentaById(@PathVariable int id) {
        Venta venta = ventaService.getVentaById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));
        return ventaAssembler.toModel(venta);
    }

    @GetMapping("/usuario/{id_usuario}")
    public CollectionModel<EntityModel<Venta>> getVentasByUsuario(@PathVariable int id_usuario) {
        List<EntityModel<Venta>> ventas = ventaService.getVentasByUsuarioId(id_usuario).stream()
            .map(ventaAssembler::toModel)
            .toList();

        return CollectionModel.of(ventas,
            linkTo(methodOn(VentaController.class).getVentasByUsuarioId(id_usuario)).withSelfRel());
    }

    @GetMapping
    public CollectionModel<EntityModel<Venta>> getAllVentas() {
        List<EntityModel<Venta>> ventas = ventaService.findAll().stream()
            .map(ventaAssembler::toModel)
            .toList();

        return CollectionModel.of(ventas,
            linkTo(methodOn(VentaController.class).getAllUsers()).withSelfRel());
    }
}