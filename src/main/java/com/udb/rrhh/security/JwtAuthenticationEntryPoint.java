package com.udb.rrhh.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Componente que maneja errores de autenticación (401 Unauthorized)
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Logger para registrar eventos de autenticación fallida
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    // Método que se ejecuta cuando ocurre un error de autenticación
    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {

        // Registra el error de autenticación
        logger.error("Responding with unauthorized error. Message - {}", e.getMessage());

        // Configura la respuesta HTTP
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE); // Tipo de contenido JSON
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Status 401

        // Crea el cuerpo de la respuesta de error
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // Código de estado
        body.put("error", "Unauthorized"); // Tipo de error
        body.put("message", "Token de acceso requerido para acceder a este recurso"); // Mensaje descriptivo
        body.put("path", httpServletRequest.getServletPath()); // Ruta que causó el error

        // Convierte el Map a JSON y lo escribe en la respuesta
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(httpServletResponse.getOutputStream(), body);
    }
}
