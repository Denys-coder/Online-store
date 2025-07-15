package Onlinestorerestapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("sessionAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) // still works for visual icon
                                .scheme("session")              // custom name for clarity
                                .description("Session-based authentication using JSESSIONID cookie")
                        )
                );
    }
}
