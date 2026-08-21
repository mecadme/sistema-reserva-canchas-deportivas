package ec.edu.ups.reservas.canchas.config;

import ec.edu.ups.reservas.canchas.domain.*;
import java.time.LocalTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner canchasIniciales(CanchaRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Cancha("Pádel 1", Deporte.PADEL, LocalTime.of(7, 0), LocalTime.of(22, 0)));
                repository.save(new Cancha("Tenis 1", Deporte.TENIS, LocalTime.of(7, 0), LocalTime.of(22, 0)));
                repository.save(new Cancha("Básquet 1", Deporte.BASQUET, LocalTime.of(8, 0), LocalTime.of(21, 0)));
            }
        };
    }
}
