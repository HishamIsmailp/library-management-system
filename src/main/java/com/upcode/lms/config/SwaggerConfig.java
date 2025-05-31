package com.upcode.lms.config;

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
        title = "Library Management System API",
        description = "A comprehensive Library Management System API built with Spring Boot. " +
                     "This system manages books, users, transactions, reservations, and fines with role-based access control.",
        version = "1.0.0",
        contact = @Contact(
            name = "Library Management Team",
            email = "team@library-system.com",
            url = "https://github.com/your-team/library-management-system"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080/api", description = "Local Development Server"),
        @Server(url = "https://api.library-system.com/api", description = "Production Server")
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Enter JWT Bearer token"
)
public class SwaggerConfig {
}