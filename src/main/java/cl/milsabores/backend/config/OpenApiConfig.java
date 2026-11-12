package cl.milsabores.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Configura Swagger: título de la documentación y el botón "Authorize" para probar con un token JWT
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Pastelería Mil Sabores",
                version = "v1",
                description = "Backend de la tienda en línea (DSY1104 - Evaluación Parcial 3). "
                        + "Para los endpoints protegidos: haz login en /api/v1/auth/login, copia el 'token' "
                        + "y pégalo en el botón Authorize."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
