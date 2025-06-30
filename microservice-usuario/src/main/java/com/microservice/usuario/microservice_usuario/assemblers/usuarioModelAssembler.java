package com.microservice.usuario.microservice_usuario.assemblers;


import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.microservice.usuario.microservice_usuario.controller.UsuarioControllerV2;
import com.microservice.usuario.microservice_usuario.model.Usuario;


@Component
public class usuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
            linkTo(methodOn(UsuarioControllerV2.class).getUsuario(usuario.getId_usuario())).withSelfRel(),
            linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withRel("usuarios"),
            linkToVentas(usuario.getId_usuario()),
            linkTo(methodOn(UsuarioControllerV2.class).actualizarUsuario(usuario.getId_usuario(), null)).withRel("actualizar"),
            linkTo(methodOn(UsuarioControllerV2.class).eliminarUsuario(usuario.getId_usuario())).withRel("eliminar")
        );
    }
    
    private Link linkToVentas(Integer usuarioId) {
        String href = "http://localhost:8080/api/v2/ventas/usuario/" + usuarioId;
        return Link.of(href, "ventas");
    }

}

