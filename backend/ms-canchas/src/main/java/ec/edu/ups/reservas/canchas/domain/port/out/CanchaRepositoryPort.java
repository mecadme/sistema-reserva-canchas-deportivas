package ec.edu.ups.reservas.canchas.domain.port.out;

import ec.edu.ups.reservas.canchas.domain.Cancha;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CanchaRepositoryPort {

    Optional<Cancha> findById(UUID id);

    List<Cancha> findAll();

    List<Cancha> findAllByActivaTrueOrderByNombre();

    boolean existsByNombreIgnoreCase(String nombre);

    Cancha save(Cancha cancha);
}
