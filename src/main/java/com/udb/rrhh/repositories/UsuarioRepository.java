package com.udb.rrhh.repositories;

import com.udb.rrhh.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    // Métodos para la relación con empleados
    Optional<Usuario> findByEmpleadoId(Long empleadoId);

    Boolean existsByEmpleadoId(Long empleadoId);
}
