package ec.edu.ups.reservas.canchas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API de Canchas", version = "v1"))
public class CanchasApplication {
    public static void main(String[] args) { SpringApplication.run(CanchasApplication.class, args); }
}
