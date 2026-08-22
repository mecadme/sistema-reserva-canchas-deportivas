package ec.edu.ups.reservas.reservas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "API de Reservas", version = "v1"))
public class ReservasApplication {
    public static void main(String[] args) { SpringApplication.run(ReservasApplication.class, args); }
}
