// Indica que este archivo pertenece al paquete `com.udb.rrhh.config`
package com.udb.rrhh.config;

// Importa las clases necesarias de Swagger/OpenAPI y Spring
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Marca esta clase como una clase de configuración de Spring
@Configuration
public class OpenApiConfig {

    // Define un Bean (componente gestionado por Spring) que configura la documentación OpenAPI/Swagger
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Sistema de Recursos Humanos UDB") // Título de la API
                        .version("1.0") // Versión de la API
                        .description("API REST para el Sistema de Gestión de Recursos Humanos de la Universidad Don Bosco") // Descripción
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))); // Licencia de la API
    }
}