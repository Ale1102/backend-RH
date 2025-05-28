package com.udb.rrhh.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 20, message = "El nombre del rol no puede exceder 20 caracteres")
    @Column(name = "nombre", unique = true, nullable = false, length = 20)
    private String nombre;

    @Size(max = 100, message = "La descripción no puede exceder 100 caracteres")
    @Column(name = "descripcion", length = 100)
    private String descripcion;

    // Constructores
    public Rol() {}

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
