package ec.edu.ups.reservas.usuarios.config;

import ec.edu.ups.reservas.usuarios.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner adminInicial(UsuarioRepository repository, PasswordEncoder encoder,
            @Value("${security.bootstrap-admin-password}") String adminPassword) {
        return args -> {
            if (!repository.existsByEmailIgnoreCase("admin@canchas.local")) {
                repository.save(new Usuario("Administrador", "admin@canchas.local",
                        encoder.encode(adminPassword), Rol.ADMINISTRADOR));
            }
        };
    }
}
