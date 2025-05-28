package com.udb.rrhh.services;

import com.udb.rrhh.dtos.EmpleadoDTO;
import com.udb.rrhh.exceptions.ResourceNotFoundException;
import com.udb.rrhh.models.Empleado;
import com.udb.rrhh.models.Usuario;
import com.udb.rrhh.repositories.EmpleadoRepository;
import com.udb.rrhh.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // === MÉTODOS EXISTENTES ===

    public List<EmpleadoDTO> getAllEmpleados() {
        return empleadoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmpleadoDTO getEmpleadoById(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));
        return convertToDTO(empleado);
    }

    public EmpleadoDTO createEmpleado(EmpleadoDTO empleadoDTO) {
        Empleado empleado = convertToEntity(empleadoDTO);
        Empleado savedEmpleado = empleadoRepository.save(empleado);
        return convertToDTO(savedEmpleado);
    }

    public EmpleadoDTO updateEmpleado(Long id, EmpleadoDTO empleadoDTO) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));

        empleado.setNombreEmp(empleadoDTO.getNombreEmp());
        empleado.setApellidoEmp(empleadoDTO.getApellidoEmp());
        empleado.setDireccion(empleadoDTO.getDireccion());
        empleado.setTelefono(empleadoDTO.getTelefono());
        empleado.setEmail(empleadoDTO.getEmail());
        empleado.setFechaNacimiento(empleadoDTO.getFechaNacimiento());
        empleado.setRepresentacion(empleadoDTO.getRepresentacion());

        Empleado updatedEmpleado = empleadoRepository.save(empleado);
        return convertToDTO(updatedEmpleado);
    }

    public void deleteEmpleado(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));
        empleadoRepository.delete(empleado);
    }

    public List<EmpleadoDTO> searchEmpleados(String query) {
        return empleadoRepository.findByNombreEmpContainingOrApellidoEmpContaining(query, query)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // === NUEVOS MÉTODOS PARA EMPLEADOS CON ACCESO LIMITADO ===

    /**
     * Obtiene lista básica de empleados (sin datos sensibles)
     * Solo nombres, apellidos y cargos - para rol EMPLEADO
     */
    public List<EmpleadoDTO> getAllEmpleadosBasicos() {
        return empleadoRepository.findAll().stream()
                .map(this::convertToDTOBasico)
                .collect(Collectors.toList());
    }

    /**
     * Búsqueda básica de empleados (sin datos sensibles)
     * Solo nombres, apellidos y cargos - para rol EMPLEADO
     */
    public List<EmpleadoDTO> searchEmpleadosBasicos(String query) {
        return empleadoRepository.findByNombreEmpContainingOrApellidoEmpContaining(query, query)
                .stream()
                .map(this::convertToDTOBasico)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene empleado por username del usuario
     * Para que un empleado pueda ver su propia información
     */
    public EmpleadoDTO getEmpleadoByUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        // Verificar si el usuario tiene un empleado asociado
        if (usuario.getEmpleadoId() == null) {
            throw new ResourceNotFoundException("El usuario '" + username + "' no tiene un empleado asociado");
        }

        return getEmpleadoById(usuario.getEmpleadoId());
    }

    /**
     * Actualiza empleado por username del usuario
     * Para que un empleado pueda actualizar su propia información (limitada)
     */
    public EmpleadoDTO updateEmpleadoByUsername(String username, EmpleadoDTO empleadoDTO) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        // Verificar si el usuario tiene un empleado asociado
        if (usuario.getEmpleadoId() == null) {
            throw new ResourceNotFoundException("El usuario '" + username + "' no tiene un empleado asociado");
        }

        Empleado empleado = empleadoRepository.findById(usuario.getEmpleadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + usuario.getEmpleadoId()));

        // Solo permite actualizar campos básicos (no sensibles)
        if (empleadoDTO.getDireccion() != null) {
            empleado.setDireccion(empleadoDTO.getDireccion());
        }
        if (empleadoDTO.getTelefono() != null) {
            empleado.setTelefono(empleadoDTO.getTelefono());
        }
        if (empleadoDTO.getEmail() != null) {
            empleado.setEmail(empleadoDTO.getEmail());
        }
        // NO permite cambiar: nombre, apellido, fecha nacimiento, representación

        Empleado updatedEmpleado = empleadoRepository.save(empleado);
        return convertToDTO(updatedEmpleado);
    }

    // === MÉTODOS DE CONVERSIÓN ===

    /**
     * Convierte a DTO completo (para ADMIN y USER)
     */
    private EmpleadoDTO convertToDTO(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setIdEmpleado(empleado.getIdEmpleado());
        dto.setNombreEmp(empleado.getNombreEmp());
        dto.setApellidoEmp(empleado.getApellidoEmp());
        dto.setDireccion(empleado.getDireccion());
        dto.setTelefono(empleado.getTelefono());
        dto.setEmail(empleado.getEmail());
        dto.setFechaNacimiento(empleado.getFechaNacimiento());
        dto.setRepresentacion(empleado.getRepresentacion());
        return dto;
    }

    /**
     * Convierte a DTO básico (para rol EMPLEADO)
     * Solo información no sensible
     */
    private EmpleadoDTO convertToDTOBasico(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setIdEmpleado(empleado.getIdEmpleado());
        dto.setNombreEmp(empleado.getNombreEmp());
        dto.setApellidoEmp(empleado.getApellidoEmp());
        // NO incluye: dirección, teléfono, email, fecha nacimiento, representación
        return dto;
    }

    private Empleado convertToEntity(EmpleadoDTO dto) {
        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(dto.getIdEmpleado());
        empleado.setNombreEmp(dto.getNombreEmp());
        empleado.setApellidoEmp(dto.getApellidoEmp());
        empleado.setDireccion(dto.getDireccion());
        empleado.setTelefono(dto.getTelefono());
        empleado.setEmail(dto.getEmail());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setRepresentacion(dto.getRepresentacion());
        return empleado;
    }
}
