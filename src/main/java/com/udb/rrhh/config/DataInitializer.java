// Indica que esta clase pertenece al paquete 'com.udb.rrhh.config'
package com.udb.rrhh.config;

// Importaciones de clases necesarias
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

// Marca esta clase como un componente de Spring (se auto-registra en el contenedor de Spring)
@Component
// Implementa CommandLineRunner para ejecutar código al iniciar la aplicación
public class DataInitializer implements CommandLineRunner {

    // Inyección de dependencias de Spring (automaticamente asigna una instancia de RolRepository)
    @Autowired
    private RolRepository rolRepository;

    // Inyección de dependencias para el repositorio de usuarios
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Inyección del codificador de contraseñas (para hashear contraseñas)
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método run() se ejecuta al iniciar la aplicación
    @Override
    // Asegura que las operaciones con la BD sean transaccionales (atomicidad)
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🚀 === INICIANDO DATA INITIALIZER ===");

        try {
            // Crea roles si no existen en la BD
            Rol adminRole = crearRolSiNoExiste("ADMIN", "Administrador del sistema - Acceso completo");
            Rol userRole = crearRolSiNoExiste("USER", "Usuario normal - Solo lectura");
            Rol empleadoRole = crearRolSiNoExiste("EMPLEADO", "Empleado - Acceso limitado a su información");

            // Crea usuarios iniciales con roles asignados
            crearUsuarioSiNoExiste("admin", "admin@udb.edu.sv", "admin123",
                    Set.of(adminRole), "Administrador del Sistema");

            crearUsuarioSiNoExiste("usuario", "usuario@udb.edu.sv", "user123",
                    Set.of(userRole), "Usuario Normal");

            crearUsuarioSiNoExiste("empleado", "empleado@udb.edu.sv", "emp123",
                    Set.of(empleadoRole), "Empleado del Sistema");

        } catch (Exception e) {
            // Manejo de errores (imprime el error en consola)
            System.out.println("❌ ERROR en DataInitializer: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("🏁 === DATA INITIALIZER COMPLETADO ===");
    }

    // Método auxiliar para crear un rol si no existe
    private Rol crearRolSiNoExiste(String nombre, String descripcion) {
        // Verifica si el rol ya existe en la BD
        if (!rolRepository.existsByNombre(nombre)) {
            // Crea un nuevo rol
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rol.setDescripcion(descripcion);
            // Guarda el rol en la BD
            rol = rolRepository.save(rol);
            System.out.println("✅ Rol " + nombre + " creado con ID: " + rol.getIdRol());
            return rol;
        } else {
            // Si el rol ya existe, lo recupera de la BD
            Rol rol = rolRepository.findByNombre(nombre).orElse(null);
            System.out.println("ℹ️ Rol " + nombre + " ya existe con ID: " + rol.getIdRol());
            return rol;
        }
    }

    // Método auxiliar para crear un usuario si no existe
    private void crearUsuarioSiNoExiste(String username, String email, String password,
                                        Set<Rol> roles, String descripcion) {
        // Verifica si el usuario ya existe en la BD
        if (!usuarioRepository.existsByUsername(username)) {
            // Hashea la contraseña antes de guardarla
            String encodedPassword = passwordEncoder.encode(password);

            // Crea un nuevo usuario y asigna sus propiedades
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setEmail(email);
            usuario.setPassword(encodedPassword);
            usuario.setActivo(true);
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setRoles(roles);

            // Guarda el usuario en la BD
            usuario = usuarioRepository.save(usuario);

            // Mensajes de confirmación en consola
            System.out.println("✅ === " + descripcion.toUpperCase() + " CREADO ===");
            System.out.println("👤 Username: " + usuario.getUsername());
            System.out.println("📧 Email: " + usuario.getEmail());
            System.out.println("🔑 Contraseña: " + password); // ¡OJO: Muestra la contraseña en texto plano!
            System.out.println("🎭 Roles: " + roles.stream().map(Rol::getNombre).toList());
        } else {
            System.out.println("ℹ️ Usuario " + username + " ya existe");
        }
    }
}