package com.hotelworks.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Hotel Management System API",
        version = "1.0.0",
        description = "Comprehensive Hotel Management System Backend API for managing reservations, check-ins, advances, post transactions, and billing operations.",
        contact = @Contact(
            name = "Hotel Management Support",
            email = "support@hotelworks.com",
            url = "https://hotelworks.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Development Server"
        ),
        @Server(
            url = "https://api.hotelworks.com",
            description = "Production Server"
        )
    }
)
@SecurityScheme(
    name = "basicAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "basic",
    description = "Basic Authentication using username and password"
)
public class OpenApiConfig {
}