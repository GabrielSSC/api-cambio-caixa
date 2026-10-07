package com.ada.caixa.transfer.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI is served at /swagger-ui.html and is public — see
 * SecurityConfig, which explicitly permits the docs endpoints while every
 * business endpoint still requires authentication. Use the "Authorize"
 * button in the UI with the instructor credentials to try requests (those
 * still need Basic Auth) from the page itself.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Câmbio e Compras",
                version = "v1",
                description = "API para cadastro e consulta de clientes, consulta de cotações de câmbio e registro e consulta de ordens de compra de moeda."
        ),
        security = @SecurityRequirement(name = "basicAuth")
)
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
public class OpenApiConfig {
}
