package com.udb.rrhh.controllers;

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
@CrossOrigin(origins = "*")
public class ContratacionController {

    @Autowired
    private ContratacionService contratacionService;

    @GetMapping
    public ResponseEntity<List<ContratacionDTO>> getAllContrataciones() {
        try {
            System.out.println("🔍 GET /api/contrataciones - Obteniendo todas las contrataciones");
            List<ContratacionDTO> contrataciones = contratacionService.getAllContrataciones();
            System.out.println("✅ Se encontraron " + contrataciones.size() + " contrataciones");
            return ResponseEntity.ok(contrataciones);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener contrataciones: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratacionDTO> getContratacionById(@PathVariable Long id) {
        try {
            System.out.println("🔍 GET /api/contrataciones/" + id);
            ContratacionDTO contratacion = contratacionService.getContratacionById(id);
            return ResponseEntity.ok(contratacion);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener contratación por ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<ContratacionDTO> createContratacion(@Valid @RequestBody ContratacionDTO contratacionDTO) {
        try {
            System.out.println("✅ POST /api/contrataciones - Creando nueva contratación");
            ContratacionDTO nuevaContratacion = contratacionService.createContratacion(contratacionDTO);
            return new ResponseEntity<>(nuevaContratacion, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("❌ Error al crear contratación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratacionDTO> updateContratacion(@PathVariable Long id, @Valid @RequestBody ContratacionDTO contratacionDTO) {
        try {
            System.out.println("📝 PUT /api/contrataciones/" + id);
            ContratacionDTO contratacionActualizada = contratacionService.updateContratacion(id, contratacionDTO);
            return ResponseEntity.ok(contratacionActualizada);
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar contratación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContratacion(@PathVariable Long id) {
        try {
            System.out.println("🗑️ DELETE /api/contrataciones/" + id);
            contratacionService.deleteContratacion(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar contratación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<ContratacionDTO>> getContratacionesByEmpleado(@PathVariable Long idEmpleado) {
        try {
            System.out.println("🔍 GET /api/contrataciones/empleado/" + idEmpleado);
            List<ContratacionDTO> contrataciones = contratacionService.getContratacionesByEmpleado(idEmpleado);
            return ResponseEntity.ok(contrataciones);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener contrataciones por empleado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/activas")
    public ResponseEntity<List<ContratacionDTO>> getContratacionesActivas() {
        try {
            System.out.println("🔍 GET /api/contrataciones/activas");
            List<ContratacionDTO> contratacionesActivas = contratacionService.getContratacionesActivas();
            System.out.println("✅ Se encontraron " + contratacionesActivas.size() + " contrataciones activas");
            return ResponseEntity.ok(contratacionesActivas);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener contrataciones activas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debugContrataciones() {
        return ResponseEntity.ok("✅ Endpoint de contrataciones funcionando correctamente");
    }
}
