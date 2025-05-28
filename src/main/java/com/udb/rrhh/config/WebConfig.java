// Indica que esta clase pertenece al paquete 'com.udb.rrhh.config'
package com.udb.rrhh.config;

// Importa las anotaciones y clases necesarias de Spring
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Marca esta clase como una configuración de Spring
@Configuration
// Implementa WebMvcConfigurer para personalizar la configuración de Spring MVC
public class WebConfig implements WebMvcConfigurer {

    // Sobrescribe el método para configurar CORS (Cross-Origin Resource Sharing)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")          // Aplica CORS a todas las rutas
                .allowedOrigins("*")       // Permite solicitudes desde cualquier origen
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Métodos HTTP permitidos
                .allowedHeaders("*")       // Permite todos los headers en las solicitudes
                .maxAge(3600);             // Tiempo de caché para las opciones CORS (en segundos)
    }
}