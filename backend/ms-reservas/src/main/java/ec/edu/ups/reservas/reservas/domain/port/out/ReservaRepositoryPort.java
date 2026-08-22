package ec.edu.ups.reservas.reservas.domain.port.out;

import ec.edu.ups.reservas.reservas.domain.EstadoReserva;
import ec.edu.ups.reservas.reservas.domain.Reserva;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservaRepositoryPort {

    Optional<Reserva> findById(UUID id);

    List<Reserva> findAll();

    List<Reserva> findByUsuarioIdOrderByInicioDesc(UUID usuarioId);

    long countByUsuarioIdAndEstadoAndInicioAfter(UUID usuarioId, EstadoReserva estado, OffsetDateTime now);

    List<Reserva> findByCanchaIdAndEstadoAndInicioLessThanAndFinGreaterThan(
            UUID canchaId, EstadoReserva estado, OffsetDateTime fin, OffsetDateTime inicio);

    List<Reserva> findByInicioGreaterThanEqualAndInicioLessThanOrderByInicio(
            OffsetDateTime desde, OffsetDateTime hasta);

    Reserva saveAndFlush(Reserva reserva);

    Reserva save(Reserva reserva);

    int finalizarVencidas(OffsetDateTime now, EstadoReserva confirmada, EstadoReserva finalizada);
}
