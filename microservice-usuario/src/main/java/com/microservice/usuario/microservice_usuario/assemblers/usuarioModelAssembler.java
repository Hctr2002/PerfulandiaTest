package com.microservice.usuario.microservice_usuario.assemblers;


import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.microservice.usuario.microservice_usuario.model.Usuario;
import com.microservice.usuario.microservice_usuario.controller.UsuarioControllerV2;


@Component
public class usuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
            linkTo(methodOn(UsuarioControllerV2.class).getUsuario(usuario.getId_usuario())).withSelfRel(),
            linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withRel("usuarios"),
            // Este enlace es hacia el microservicio de ventas (ajusta si usas API Gateway)
            // También puedes usar Feign para resolver dinámicamente si lo necesitas
            linkToVentas(usuario.getId_usuario())
        );
    }
    
    private Link linkToVentas(Integer usuarioId) {
        String href = "http://localhost:8080/api/v1/ventas/usuario/" + usuarioId;
        return Link.of(href, "ventas");
    }

}

