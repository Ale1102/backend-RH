package com.udb.rrhh.controllers;

import com.udb.rrhh.dtos.EmpleadoDTO;
import com.udb.rrhh.services.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "*")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    // === ENDPOINTS PARA ADMIN Y USER ===

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('EMPLEADO')")
    public ResponseEntity<List<EmpleadoDTO>> getAllEmpleados() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // Si es EMPLEADO, solo puede ver empleados básicos (sin datos sensibles)
        if (role.equals("ROLE_EMPLEADO")) {
            return ResponseEntity.ok(empleadoService.getAllEmpleadosBasicos());
        }

        return ResponseEntity.ok(empleadoService.getAllEmpleados());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('EMPLEADO')")
    public ResponseEntity<EmpleadoDTO> getEmpleadoById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // Si es EMPLEADO, solo puede ver su propia información
        if (role.equals("ROLE_EMPLEADO")) {
            return ResponseEntity.ok(empleadoService.getEmpleadoByUsername(username));
        }

        return ResponseEntity.ok(empleadoService.getEmpleadoById(id));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('EMPLEADO')")
    public ResponseEntity<List<EmpleadoDTO>> searchEmpleados(@RequestParam String query) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // Si es EMPLEADO, búsqueda limitada
        if (role.equals("ROLE_EMPLEADO")) {
            return ResponseEntity.ok(empleadoService.searchEmpleadosBasicos(query));
        }

        return ResponseEntity.ok(empleadoService.searchEmpleados(query));
    }

    // === ENDPOINTS SOLO PARA ADMIN ===

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoDTO> createEmpleado(@Valid @RequestBody EmpleadoDTO empleadoDTO) {
        return new ResponseEntity<>(empleadoService.createEmpleado(empleadoDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmpleadoDTO> updateEmpleado(@PathVariable Long id, @Valid @RequestBody EmpleadoDTO empleadoDTO) {
        return ResponseEntity.ok(empleadoService.updateEmpleado(id, empleadoDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long id) {
        empleadoService.deleteEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    // === ENDPOINTS ESPECIALES PARA EMPLEADOS ===

    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<EmpleadoDTO> getMiPerfil() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(empleadoService.getEmpleadoByUsername(username));
    }

    @PutMapping("/mi-perfil")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<EmpleadoDTO> updateMiPerfil(@Valid @RequestBody EmpleadoDTO empleadoDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(empleadoService.updateEmpleadoByUsername(username, empleadoDTO));
    }
}
