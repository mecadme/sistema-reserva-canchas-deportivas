package ec.edu.ups.reservas.reservas.domain.port.out;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface CanchaServicePort {

    CanchaDetalle obtener(UUID id, OffsetDateTime inicio, OffsetDateTime fin);

    List<RangoBloqueo> bloqueos(UUID canchaId, OffsetDateTime inicio, OffsetDateTime fin);

    record CanchaDetalle(UUID id, String nombre, String deporte,
                         LocalTime horaApertura, LocalTime horaCierre,
                         boolean activa, boolean bloqueada) {}

    record RangoBloqueo(OffsetDateTime inicio, OffsetDateTime fin) {}
}
