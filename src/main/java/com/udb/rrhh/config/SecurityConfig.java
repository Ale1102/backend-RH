// Indica que esta clase pertenece al paquete 'com.udb.rrhh.config'
package com.udb.rrhh.config;

// Importa las clases necesarias para la configuración de seguridad
import com.udb.rrhh.security.CustomUserDetailsService;
import com.udb.rrhh.security.JwtAuthenticationEntryPoint;
import com.udb.rrhh.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

// Marca esta clase como una configuración de Spring
@Configuration
// Habilita la seguridad web de Spring Security
@EnableWebSecurity
// Permite el uso de anotaciones como @PreAuthorize para seguridad a nivel de método
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // Inyecta el servicio personalizado de detalles de usuario
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Inyecta el manejador de errores de autenticación JWT
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    // Define un Bean para el filtro de autenticación JWT
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    // Define un Bean para el codificador de contraseñas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura el proveedor de autenticación con el servicio de usuarios y el codificador de contraseñas
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Define un Bean para el administrador de autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Configura la cadena de filtros de seguridad y las reglas de autorización
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Habilita CORS con la configuración definida
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Deshabilita CSRF (no es necesario para APIs REST con JWT)
                .csrf(csrf -> csrf.disable())
                // Configura el manejador de excepciones para errores de autenticación
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                // Establece la política de sesión como STATELESS (sin sesión)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Define las reglas de autorización para los endpoints
                .authorizeHttpRequests(authz -> authz
                        // === ENDPOINTS PÚBLICOS ===
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // === EMPLEADOS ===
                        .requestMatchers(HttpMethod.GET, "/api/empleados/**").hasAnyRole("ADMIN", "USER", "EMPLEADO")
                        .requestMatchers(HttpMethod.POST, "/api/empleados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/empleados/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/empleados/**").hasRole("ADMIN")

                        // === DEPARTAMENTOS ===
                        .requestMatchers(HttpMethod.GET, "/api/departamentos/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/departamentos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/departamentos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/departamentos/**").hasRole("ADMIN")

                        // === CARGOS ===
                        .requestMatchers(HttpMethod.GET, "/api/cargos/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/cargos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cargos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cargos/**").hasRole("ADMIN")

                        // === CONTRATACIONES ===
                        .requestMatchers(HttpMethod.GET, "/api/contrataciones/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/contrataciones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/contrataciones/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/contrataciones/**").hasRole("ADMIN")

                        // === TIPOS DE CONTRATACIÓN ===
                        .requestMatchers(HttpMethod.GET, "/api/tipos-contratacion/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/tipos-contratacion/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tipos-contratacion/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tipos-contratacion/**").hasRole("ADMIN")

                        // === GESTIÓN DE USUARIOS ===
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                );

        // Añade el proveedor de autenticación y el filtro JWT
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configura CORS para permitir solicitudes desde cualquier origen
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}