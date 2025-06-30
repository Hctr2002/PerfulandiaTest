package com.microservice.venta.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.microservice.venta.controller.VentaControllerV2;
import com.microservice.venta.model.Venta;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ventaModelAssembler implements RepresentationModelAssembler<Venta, EntityModel<Venta>> {

    @Override
    public EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
            linkTo(methodOn(VentaControllerV2.class).getVentaById(venta.getId_venta())).withSelfRel(),
            linkTo(methodOn(VentaControllerV2.class).getVentasByUsuario(venta.getIdUsuario())).withRel("ventas-del-usuario"),
            linkTo(methodOn(VentaControllerV2.class).getAllVentas()).withRel("todas-las-ventas")
        );
    }
}
