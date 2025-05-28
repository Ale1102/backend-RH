package com.udb.rrhh.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filtro que se ejecuta una vez por request para validar tokens JWT
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Inyección del proveedor de tokens JWT
    @Autowired
    private JwtTokenProvider tokenProvider;

    // Inyección del servicio de detalles de usuario
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Logger para registrar eventos y errores
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Método principal del filtro que se ejecuta en cada request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extrae el token JWT del header Authorization
            String jwt = getJwtFromRequest(request);

            // Verifica si el token existe y es válido
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Extrae el username del token
                String username = tokenProvider.getUsernameFromToken(jwt);

                // Carga los detalles del usuario desde la base de datos
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // Crea el objeto de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, // Principal (usuario)
                                null, // Credentials (no necesarias después de autenticación)
                                userDetails.getAuthorities() // Autoridades/roles
                        );

                // Establece detalles adicionales de la autenticación
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establece la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Registra cualquier error durante el proceso de autenticación
            logger.error("Could not set user authentication in security context", ex);
        }

        // Continúa con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    // Método privado para extraer el token JWT del header Authorization
    private String getJwtFromRequest(HttpServletRequest request) {
        // Obtiene el valor del header Authorization
        String bearerToken = request.getHeader("Authorization");

        // Verifica si el header existe y comienza con "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Retorna el token sin el prefijo "Bearer "
        }
        return null; // No hay token válido
    }
}
