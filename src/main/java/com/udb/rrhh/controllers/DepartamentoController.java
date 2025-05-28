package com.udb.rrhh.controllers;

// Importaciones estándar para un controlador Spring
import com.udb.rrhh.dtos.DepartamentoDTO;
import com.udb.rrhh.services.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

// Configuración básica del controlador
@RestController
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*") // Configuración CORS para desarrollo
public class DepartamentoController {

    @Autowired
    private DepartamentoService departamentoService; // Inyección de dependencia

    // =============== ENDPOINTS PRINCIPALES ===============

    /**
     * Obtiene todos los departamentos
     * GET /api/departamentos
     */
    @GetMapping
    public ResponseEntity<List<DepartamentoDTO>> getAll() {
        return ResponseEntity.ok(departamentoService.getAllDepartamentos());
    }

    /**
     * Obtiene un departamento específico por ID
     * GET /api/departamentos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departamentoService.getDepartamentoById(id));
    }

    // =============== ENDPOINTS DE MODIFICACIÓN ===============

    /**
     * Crea un nuevo departamento
     * POST /api/departamentos
     */
    @PostMapping
    public ResponseEntity<DepartamentoDTO> create(@Valid @RequestBody DepartamentoDTO dto) {
        return new ResponseEntity<>(
                departamentoService.createDepartamento(dto),
                HttpStatus.CREATED
        );
    }

    /**
     * Actualiza un departamento existente
     * PUT /api/departamentos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DepartamentoDTO dto) {
        return ResponseEntity.ok(departamentoService.updateDepartamento(id, dto));
    }

    /**
     * Elimina un departamento
     * DELETE /api/departamentos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departamentoService.deleteDepartamento(id);
        return ResponseEntity.noContent().build();
    }

    // =============== ENDPOINT ADICIONAL ===============

    /**
     * Búsqueda de departamentos
     * GET /api/departamentos/search?query=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<DepartamentoDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(departamentoService.searchDepartamentos(query));
    }
}