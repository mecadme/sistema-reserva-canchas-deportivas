package ec.edu.ups.reservas.canchas.domain;

import ec.edu.ups.reservas.canchas.domain.port.out.CanchaRepositoryPort;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CanchaRepository extends JpaRepository<Cancha, UUID>, CanchaRepositoryPort {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, UUID id);

    @Override
    Cancha save(Cancha cancha);
}
