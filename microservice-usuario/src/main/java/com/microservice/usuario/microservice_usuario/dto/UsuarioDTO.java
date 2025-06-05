package com.microservice.usuario.microservice_usuario.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    
    private int id_usuario;
    private String rut;
    private String nombres;
    private String apellidos;
    private String correo;
    

}
