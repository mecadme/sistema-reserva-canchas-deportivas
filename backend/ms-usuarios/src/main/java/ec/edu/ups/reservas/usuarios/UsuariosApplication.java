package ec.edu.ups.reservas.usuarios;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API de Usuarios", version = "v1"))
public class UsuariosApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsuariosApplication.class, args);
    }
}
