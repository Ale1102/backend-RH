package com.udb.rrhh.controllers;

// Importaciones
import com.udb.rrhh.dtos.ContratacionDTO;
import com.udb.rrhh.services.ContratacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/contrataciones")
@CrossOrigin(origins = "*") // Permite CORS para desarrollo
public class ContratacionController {

    @Autowired
    private ContratacionService contratacionService; // Inyección del servicio

    // ====================== ENDPOINTS CRUD BÁSICO ======================

    /**
     * Obtiene todas las contrataciones
     * GET /api/contrataciones
     */
    @GetMapping
    public ResponseEntity<List<ContratacionDTO>> getAllContrataciones() {
        try {
            System.out.println("🔍 Obteniendo todas las contrataciones");
            List<ContratacionDTO> contrataciones = contratacionService.getAllContrataciones();
            System.out.println("✅ Encontradas: " + contrataciones.size());
            return ResponseEntity.ok(contrataciones);
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene una contratación por ID
     * GET /api/contrataciones/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContratacionDTO> getContratacionById(@PathVariable Long id) {
        try {
            ContratacionDTO contratacion = contratacionService.getContratacionById(id);
            return ResponseEntity.ok(contratacion);
        } catch (Exception e) {
            System.err.println("❌ No encontrada ID: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    // ====================== ENDPOINTS ESPECIALIZADOS ======================

    /**
     * Obtiene contrataciones de un empleado específico
     * GET /api/contrataciones/empleado/{idEmpleado}
     */
    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<ContratacionDTO>> getByEmpleado(@PathVariable Long idEmpleado) {
        try {
            List<ContratacionDTO> contrataciones = contratacionService.getContratacionesByEmpleado(idEmpleado);
            return ResponseEntity.ok(contrataciones);
        } catch (Exception e) {
            System.err.println("❌ Error con empleado ID: " + idEmpleado);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene solo contrataciones activas
     * GET /api/contrataciones/activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<ContratacionDTO>> getContratacionesActivas() {
        try {
            List<ContratacionDTO> activas = contratacionService.getContratacionesActivas();
            return ResponseEntity.ok(activas);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener activas");
            return ResponseEntity.internalServerError().build();
        }
    }

    // ====================== ENDPOINTS DE DIAGNÓSTICO ======================

    /**
     * Endpoint de prueba
     * GET /api/contrataciones/debug
     */
    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        return ResponseEntity.ok("✅ Servicio de contrataciones operativo");
    }
}