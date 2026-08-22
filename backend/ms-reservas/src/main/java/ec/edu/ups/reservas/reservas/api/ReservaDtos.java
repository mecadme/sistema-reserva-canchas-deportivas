package ec.edu.ups.reservas.reservas.api;

import ec.edu.ups.reservas.reservas.domain.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public final class ReservaDtos {

    private ReservaDtos() {}

    public record CrearReservaRequest(
            @NotNull UUID canchaId,
            @NotNull OffsetDateTime inicio) {}

    public record CancelarReservaRequest(@Size(max = 250) String motivo) {}

    public record ReservaResponse(
            UUID id,
            UUID usuarioId,
            UUID canchaId,
            String canchaNombre,
            String deporte,
            OffsetDateTime inicio,
            OffsetDateTime fin,
            EstadoReserva estado,
            UUID canceladaPor,
            String motivoCancelacion) {

        public static ReservaResponse from(Reserva reserva) {
            return new ReservaResponse(
                    reserva.getId(),
                    reserva.getUsuarioId(),
                    reserva.getCanchaId(),
                    reserva.getCanchaNombre(),
                    reserva.getDeporte(),
                    reserva.getInicio(),
                    reserva.getFin(),
                    reserva.getEstado(),
                    reserva.getCanceladaPor(),
                    reserva.getMotivoCancelacion());
        }
    }

    public record SlotResponse(OffsetDateTime inicio, OffsetDateTime fin, String estado) {}

    public record DisponibilidadResponse(UUID canchaId, LocalDate fecha, List<SlotResponse> bloques) {}

    public record ResumenReporteResponse(
            long total,
            long confirmadas,
            long canceladas,
            long finalizadas,
            Map<String, Long> porCancha,
            Map<String, Long> ocupadasPorCancha,
            Map<String, Long> porDeporte) {}
}
