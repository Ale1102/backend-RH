package com.udb.rrhh.security;

import com.udb.rrhh.models.Usuario;
import com.udb.rrhh.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Servicio personalizado para cargar detalles del usuario en Spring Security
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Inyección del repositorio de usuarios
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Implementación del método de UserDetailsService
    // Este método es llamado por Spring Security durante la autenticación
    @Override
    @Transactional // Asegura que la transacción esté activa para lazy loading
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario por username en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con username: " + username)
                );

        // Convierte la entidad Usuario a UserPrincipal (que implementa UserDetails)
        return UserPrincipal.create(usuario);
    }

    // Método adicional para cargar usuario por ID
    // Útil para operaciones que requieren el ID del usuario
    @Transactional
    public UserDetails loadUserById(Long id) {
        // Busca el usuario por ID en la base de datos
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con id: " + id)
                );

        // Convierte la entidad Usuario a UserPrincipal
        return UserPrincipal.create(usuario);
    }
}
