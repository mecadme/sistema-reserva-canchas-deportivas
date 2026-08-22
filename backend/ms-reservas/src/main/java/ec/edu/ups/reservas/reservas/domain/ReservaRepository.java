package ec.edu.ups.reservas.reservas.domain;

import ec.edu.ups.reservas.reservas.domain.port.out.ReservaRepositoryPort;
import java.time.OffsetDateTime;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, UUID>, ReservaRepositoryPort {

    List<Reserva> findByUsuarioIdOrderByInicioDesc(UUID usuarioId);

    long countByUsuarioIdAndEstadoAndInicioAfter(UUID usuarioId, EstadoReserva estado, OffsetDateTime now);

    List<Reserva> findByCanchaIdAndEstadoAndInicioLessThanAndFinGreaterThan(
            UUID canchaId, EstadoReserva estado, OffsetDateTime fin, OffsetDateTime inicio);

    List<Reserva> findByInicioGreaterThanEqualAndInicioLessThanOrderByInicio(
            OffsetDateTime desde, OffsetDateTime hasta);

    @Modifying
    @Query("update Reserva r set r.estado = :finalizada, r.actualizadoEn = :now where r.estado = :confirmada and r.fin <= :now")
    int finalizarVencidas(@Param("now") OffsetDateTime now,
                          @Param("confirmada") EstadoReserva confirmada,
                          @Param("finalizada") EstadoReserva finalizada);

    @Override
    Reserva save(Reserva reserva);

    @Override
    Reserva saveAndFlush(Reserva reserva);
}
