package ec.edu.ups.reservas.canchas.domain.port.out;

import ec.edu.ups.reservas.canchas.domain.BloqueoMantenimiento;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface BloqueoRepositoryPort {

    List<BloqueoMantenimiento> findByCanchaIdOrderByInicio(UUID canchaId);

    boolean existeSolapamiento(UUID canchaId, OffsetDateTime inicio, OffsetDateTime fin);

    List<BloqueoMantenimiento> findSolapamientos(UUID canchaId, OffsetDateTime inicio, OffsetDateTime fin);

    BloqueoMantenimiento save(BloqueoMantenimiento bloqueo);
}
