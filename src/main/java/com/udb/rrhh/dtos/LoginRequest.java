package com.udb.rrhh.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Anotaciones de Lombok para reducir código boilerplate
@Data                // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor   // Crea constructor sin argumentos
@AllArgsConstructor  // Crea constructor con todos los argumentos
public class LoginRequest {

    // Validación: el username no puede estar vacío
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    // Validación: la contraseña no puede estar vacía
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}