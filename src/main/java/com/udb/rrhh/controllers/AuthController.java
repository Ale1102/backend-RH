// Indica que esta clase pertenece al paquete 'com.udb.rrhh.controllers'
package com.udb.rrhh.controllers;

// Importa los DTOs (objetos de transferencia de datos) y clases de seguridad
import com.udb.rrhh.dtos.LoginRequest;
import com.udb.rrhh.dtos.LoginResponse;
import com.udb.rrhh.dtos.RegistroRequest;
import com.udb.rrhh.dtos.UsuarioDTO;
import com.udb.rrhh.security.JwtTokenProvider;
import com.udb.rrhh.security.UserPrincipal;
import com.udb.rrhh.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

// Marca esta clase como un controlador REST (maneja peticiones HTTP)
@RestController
// Define la ruta base para todos los endpoints en este controlador: '/api/auth'
@RequestMapping("/api/auth")
// Permite solicitudes CORS desde cualquier origen (útil para desarrollo)
@CrossOrigin(origins = "*")
public class AuthController {

    // Inyecta el AuthenticationManager de Spring Security para autenticar usuarios
    @Autowired
    private AuthenticationManager authenticationManager;

    // Inyecta el proveedor de tokens JWT (para generar y validar tokens)
    @Autowired
    private JwtTokenProvider tokenProvider;

    // Inyecta el servicio de usuarios para operaciones como registro y actualización
    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para autenticar usuarios (login)
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Autentica al usuario usando Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Establece la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Genera un token JWT para el usuario autenticado
            String jwt = tokenProvider.generateToken(authentication);

            // Obtiene los detalles del usuario (UserPrincipal) y sus roles
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Set<String> roles = userPrincipal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.replace("ROLE_", "")) // Elimina el prefijo "ROLE_"
                    .collect(Collectors.toSet());

            // Actualiza la fecha de último acceso del usuario
            usuarioService.updateLastAccess(userPrincipal.getUsername());

            // Retorna la respuesta con el token, username, email y roles
            return ResponseEntity.ok(new LoginResponse(jwt, userPrincipal.getUsername(),
                    userPrincipal.getEmail(), roles));
        } catch (Exception e) {
            // Si la autenticación falla, retorna un error 401 (Unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        }
    }

    // Endpoint para registrar nuevos usuarios
    @PostMapping("/registro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistroRequest registroRequest) {
        try {
            // Crea un nuevo usuario usando el servicio
            UsuarioDTO usuario = usuarioService.createUsuario(registroRequest);

            // Retorna el usuario creado con código 201 (CREATED)
            return new ResponseEntity<>(usuario, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Si hay un error (ej: username ya existe), retorna 400 (Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para obtener los datos del usuario actualmente autenticado
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        // Verifica si el usuario está autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        // Obtiene los detalles del usuario y su información desde la BD
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UsuarioDTO usuario = usuarioService.getUsuarioById(userPrincipal.getId());

        // Retorna los datos del usuario
        return ResponseEntity.ok(usuario);
    }
}