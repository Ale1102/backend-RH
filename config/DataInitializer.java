package com.udb.rrhh.config;

import com.udb.rrhh.models.Rol;
import com.udb.rrhh.models.Usuario;
import com.udb.rrhh.repositories.RolRepository;
import com.udb.rrhh.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🚀 === INICIANDO DATA INITIALIZER ===");

        try {
            // Crear roles si no existen
            Rol adminRole = crearRolSiNoExiste("ADMIN", "Administrador del sistema - Acceso completo");
            Rol userRole = crearRolSiNoExiste("USER", "Usuario normal - Solo lectura");
            Rol empleadoRole = crearRolSiNoExiste("EMPLEADO", "Empleado - Acceso limitado a su información");

            // Crear usuario administrador
            crearUsuarioSiNoExiste("admin", "admin@udb.edu.sv", "admin123",
                    Set.of(adminRole), "Administrador del Sistema");

            // Crear usuario normal con accesos limitados
            crearUsuarioSiNoExiste("usuario", "usuario@udb.edu.sv", "user123",
                    Set.of(userRole), "Usuario Normal");

            // Crear usuario empleado con accesos muy limitados
            crearUsuarioSiNoExiste("empleado", "empleado@udb.edu.sv", "emp123",
                    Set.of(empleadoRole), "Empleado del Sistema");

        } catch (Exception e) {
            System.out.println("❌ ERROR en DataInitializer: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("🏁 === DATA INITIALIZER COMPLETADO ===");
    }

    private Rol crearRolSiNoExiste(String nombre, String descripcion) {
        if (!rolRepository.existsByNombre(nombre)) {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rol.setDescripcion(descripcion);
            rol = rolRepository.save(rol);
            System.out.println("✅ Rol " + nombre + " creado con ID: " + rol.getIdRol());
            return rol;
        } else {
            Rol rol = rolRepository.findByNombre(nombre).orElse(null);
            System.out.println("ℹ️ Rol " + nombre + " ya existe con ID: " + rol.getIdRol());
            return rol;
        }
    }

    private void crearUsuarioSiNoExiste(String username, String email, String password,
                                        Set<Rol> roles, String descripcion) {
        if (!usuarioRepository.existsByUsername(username)) {
            String encodedPassword = passwordEncoder.encode(password);

            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setEmail(email);
            usuario.setPassword(encodedPassword);
            usuario.setActivo(true);
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setRoles(roles);

            usuario = usuarioRepository.save(usuario);

            System.out.println("✅ === " + descripcion.toUpperCase() + " CREADO ===");
            System.out.println("👤 Username: " + usuario.getUsername());
            System.out.println("📧 Email: " + usuario.getEmail());
            System.out.println("🔑 Contraseña: " + password);
            System.out.println("🎭 Roles: " + roles.stream().map(Rol::getNombre).toList());
        } else {
            System.out.println("ℹ️ Usuario " + username + " ya existe");
        }
    }
}
