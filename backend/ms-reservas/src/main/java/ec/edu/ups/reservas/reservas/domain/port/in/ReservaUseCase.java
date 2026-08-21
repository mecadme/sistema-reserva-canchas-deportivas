package ec.edu.ups.reservas.reservas.domain.port.in;

import ec.edu.ups.reservas.reservas.api.ReservaDtos.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservaUseCase {

    ReservaResponse crear(UUID usuarioId, CrearReservaRequest request);

    List<ReservaResponse> mias(UUID usuarioId);

    List<ReservaResponse> todas();

    ReservaResponse cancelar(UUID id, UUID actorId, boolean administrador, String motivo);

    DisponibilidadResponse disponibilidad(UUID canchaId, LocalDate fecha);

    ResumenReporteResponse resumen(OffsetDateTime desde, OffsetDateTime hasta);

    void finalizarVencidas();
}
