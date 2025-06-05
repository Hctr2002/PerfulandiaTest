package com.microservice.usuario.microservice_usuario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id_usuario;

    @Column(name="rut", unique=true, length=13, nullable=false)
    @NotBlank(message="Rut es obligatorio")
    @Pattern(
        regexp="^[0-9]{7,8}-[0-9Kk]$",
        message= "El rut debe tener formato 12345678-9 (Chile)"
    )
    private String rut;

    @Column(nullable=false)
    @NotBlank(message="Los nombres son obligatorios")
    @Size(min=2, max=100, message="Los nombres deben tener entre 2 y 100 caracteres")
    private String nombres;

    @Column(nullable=false)
    @NotBlank(message="Los nombres son obligatorios")
    @Size(min=2, max=100, message="Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellidos;

    @Column(nullable=false, unique=true)
    @NotBlank(message="El correo es obligatorio")
    @Email(message="El formato del correo no es válido")
    private String correo;
    

}
