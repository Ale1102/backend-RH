package com.udb.rrhh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Anotaciones Lombok para generación automática de código
@Data  // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor  // Constructor sin parámetros
@AllArgsConstructor  // Constructor con todos los parámetros
public class CargoDTO {

    private Long idCargo;  // Identificador único

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 50, message = "El cargo no puede exceder los 50 caracteres")
    private String cargo;  // Nombre del puesto

    private String descripcionCargo;  // Detalles adicionales
}