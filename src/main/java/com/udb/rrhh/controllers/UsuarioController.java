package com.udb.rrhh.controllers;

import com.udb.rrhh.dtos.UsuarioDTO;
import com.udb.rrhh.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")  // Ruta base
@CrossOrigin(origins = "*")  // Configuración CORS
@PreAuthorize("hasRole('ADMIN')")  // Seguridad a nivel de clase
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;  // Inyección del servicio

    // ====================== ENDPOINTS ======================

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAll() {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.updateUsuario(id, usuarioDTO));
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}