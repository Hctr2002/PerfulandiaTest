package com.microservice.venta.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.microservice.venta.controller.VentaControllerV2;
import com.microservice.venta.model.Venta;

@Component
public class ventaModelAssembler implements RepresentationModelAssembler<Venta, EntityModel<Venta>> {

    @Override
    public EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
            linkTo(methodOn(VentaControllerV2.class).getVentaById(venta.getId_venta())).withSelfRel(),
            linkTo(methodOn(VentaControllerV2.class).getVentasByUsuario(venta.getIdUsuario())).withRel("ventas-del-usuario"),
            linkTo(methodOn(VentaControllerV2.class).getAllVentas()).withRel("todas-las-ventas"),
            linkToUsuario(venta.getIdUsuario())
        );
    }

    private Link linkToUsuario(Integer usuarioId) {
        String href = "http://localhost:8080/api/v2/usuarios/" + usuarioId;
        return Link.of(href, "usuario");
    }

}
