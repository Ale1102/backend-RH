package com.udb.rrhh.services;

import com.udb.rrhh.dtos.RegistroRequest;
import com.udb.rrhh.dtos.UsuarioDTO;
import com.udb.rrhh.exceptions.ResourceNotFoundException;
import com.udb.rrhh.models.Empleado;
import com.udb.rrhh.models.Rol;
import com.udb.rrhh.models.Usuario;
import com.udb.rrhh.repositories.EmpleadoRepository;
import com.udb.rrhh.repositories.RolRepository;
import com.udb.rrhh.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // === MÉTODOS PRINCIPALES ===

    public List<UsuarioDTO> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return convertToDTO(usuario);
    }

    public UsuarioDTO createUsuario(RegistroRequest registroRequest) {
        // Verificar si el username ya existe
        if (usuarioRepository.existsByUsername(registroRequest.getUsername())) {
            throw new RuntimeException("Error: El nombre de usuario ya está en uso!");
        }

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            throw new RuntimeException("Error: El email ya está en uso!");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(registroRequest.getUsername());
        usuario.setEmail(registroRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
        usuario.setFechaCreacion(LocalDateTime.now());

        // Asignar roles
        Set<String> strRoles = registroRequest.getRoles();
        Set<Rol> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Rol userRole = rolRepository.findByNombre("USER")
                    .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Rol rol = rolRepository.findByNombre(role.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Error: Rol " + role + " no encontrado."));
                roles.add(rol);
            });
        }

        usuario.setRoles(roles);

        // === CORREGIDO: Asignar empleado usando empleadoId ===
        if (registroRequest.getIdEmpleado() != null) {
            // Verificar que el empleado existe
            Empleado empleado = empleadoRepository.findById(registroRequest.getIdEmpleado())
                    .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + registroRequest.getIdEmpleado()));

            // Verificar que el empleado no esté ya asociado a otro usuario
            if (usuarioRepository.existsByEmpleadoId(registroRequest.getIdEmpleado())) {
                throw new RuntimeException("Error: El empleado ya está asociado a otro usuario!");
            }

            // Asignar el ID del empleado
            usuario.setEmpleadoId(empleado.getIdEmpleado());
        }

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }

    public UsuarioDTO updateUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Verificar si el username ya existe (excluyendo el usuario actual)
        if (!usuario.getUsername().equals(usuarioDTO.getUsername()) &&
                usuarioRepository.existsByUsername(usuarioDTO.getUsername())) {
            throw new RuntimeException("Error: El nombre de usuario ya está en uso!");
        }

        // Verificar si el email ya existe (excluyendo el usuario actual)
        if (!usuario.getEmail().equals(usuarioDTO.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("Error: El email ya está en uso!");
        }

        usuario.setUsername(usuarioDTO.getUsername());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setActivo(usuarioDTO.getActivo());

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(updatedUsuario);
    }

    public void deleteUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuarioRepository.delete(usuario);
    }

    public void updateLastAccess(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    // === MÉTODO DE CONVERSIÓN ===
    private UsuarioDTO convertToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setUsername(usuario.getUsername());
        dto.setEmail(usuario.getEmail());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());

        // Convertir roles
        if (usuario.getRoles() != null) {
            dto.setRoles(usuario.getRoles().stream()
                    .map(Rol::getNombre)
                    .collect(Collectors.toSet()));
        }

        // === CORREGIDO: Obtener información del empleado usando empleadoId ===
        if (usuario.getEmpleadoId() != null) {
            Optional<Empleado> empleadoOpt = empleadoRepository.findById(usuario.getEmpleadoId());
            if (empleadoOpt.isPresent()) {
                Empleado empleado = empleadoOpt.get();
                dto.setIdEmpleado(empleado.getIdEmpleado());
                dto.setNombreEmpleado(empleado.getNombreEmp() + " " + empleado.getApellidoEmp());
            }
        }

        return dto;
    }
}
