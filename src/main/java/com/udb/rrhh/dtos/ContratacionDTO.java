package com.udb.rrhh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data  // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor  // Constructor sin parámetros
@AllArgsConstructor  // Constructor con todos los parámetros
public class ContratacionDTO {

    private Long idContratacion;  // Identificador único

    @NotNull(message = "La fecha es obligatoria")
    private Date fecha;  // Fecha de contratación

    @NotNull(message = "El departamento es obligatorio")
    private Long idDepartamento;  // ID del departamento

    @NotNull(message = "El empleado es obligatorio")
    private Long idEmpleado;  // ID del empleado

    @NotNull(message = "El cargo es obligatorio")
    private Long idCargo;  // ID del cargo

    @NotNull(message = "El tipo de contratación es obligatorio")
    private Long idTipoContratacion;  // ID del tipo de contratación

    private Double salario;  // Salario (opcional)

    private Integer duracion;  // Duración en meses (opcional)

    private Boolean estado;  // Estado activo/inactivo (opcional)
}