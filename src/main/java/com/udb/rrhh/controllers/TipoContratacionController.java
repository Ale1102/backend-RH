package com.udb.rrhh.controllers;

// Dependencias necesarias
import com.udb.rrhh.dtos.TipoContratacionDTO;
import com.udb.rrhh.services.TipoContratacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

// Configuración básica del controlador
@RestController
@RequestMapping("/api/tipos-contratacion")  // Ruta base
@CrossOrigin(origins = "*")  // Permite CORS para desarrollo
public class TipoContratacionController {

    @Autowired
    private TipoContratacionService service;  // Capa de servicio inyectada

    // ====================== ENDPOINTS CRUD ======================

    // GET ALL: Obtener todos los tipos
    @GetMapping
    public ResponseEntity<List<TipoContratacionDTO>> getAll() {
        return ResponseEntity.ok(service.getAllTiposContratacion());
    }

    // GET BY ID: Obtener un tipo específico
    @GetMapping("/{id}")
    public ResponseEntity<TipoContratacionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTipoContratacionById(id));
    }

    // POST: Crear nuevo tipo
    @PostMapping
    public ResponseEntity<TipoContratacionDTO> create(
            @Valid @RequestBody TipoContratacionDTO dto) {
        return new ResponseEntity<>(
                service.createTipoContratacion(dto),
                HttpStatus.CREATED
        );
    }

    // PUT: Actualizar tipo existente
    @PutMapping("/{id}")
    public ResponseEntity<TipoContratacionDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TipoContratacionDTO dto) {
        return ResponseEntity.ok(service.updateTipoContratacion(id, dto));
    }

    // DELETE: Eliminar tipo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTipoContratacion(id);
        return ResponseEntity.noContent().build();
    }
}