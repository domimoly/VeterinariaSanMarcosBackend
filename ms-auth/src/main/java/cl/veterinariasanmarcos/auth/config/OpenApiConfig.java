package cl.veterinariasanmarcos.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
  title = "Veterinaria San Marcos - Auth API",
  version = "1.0",
  description = "Autenticación y emisión de JWT para tutores y recepcionistas"
))
public class OpenApiConfig {}
