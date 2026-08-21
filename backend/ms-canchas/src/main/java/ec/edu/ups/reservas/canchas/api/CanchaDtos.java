package ec.edu.ups.reservas.canchas.api;

import ec.edu.ups.reservas.canchas.domain.*;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.UUID;

public final class CanchaDtos {

    private CanchaDtos() {}

    public record CanchaRequest(
            @NotBlank @Size(max = 120) String nombre,
            @NotNull Deporte deporte,
            @NotNull LocalTime horaApertura,
            @NotNull LocalTime horaCierre) {}

    public record EstadoRequest(boolean activa) {}

    public record MantenimientoRequest(
            @NotNull OffsetDateTime inicio,
            @NotNull OffsetDateTime fin,
            @NotBlank @Size(max = 250) String motivo) {}

    public record CanchaResponse(UUID id, String nombre, Deporte deporte,
                                 LocalTime horaApertura, LocalTime horaCierre, boolean activa) {
        public static CanchaResponse from(Cancha c) {
            return new CanchaResponse(
                    c.getId(), c.getNombre(), c.getDeporte(),
                    c.getHoraApertura(), c.getHoraCierre(), c.isActiva());
        }
    }

    public record MantenimientoResponse(UUID id, UUID canchaId,
                                        OffsetDateTime inicio, OffsetDateTime fin, String motivo) {
        public static MantenimientoResponse from(BloqueoMantenimiento b) {
            return new MantenimientoResponse(
                    b.getId(), b.getCanchaId(), b.getInicio(), b.getFin(), b.getMotivo());
        }
    }

    public record CanchaInternaResponse(UUID id, String nombre, Deporte deporte,
                                        LocalTime horaApertura, LocalTime horaCierre,
                                        boolean activa, boolean bloqueada) {}

    public record BloqueoRango(OffsetDateTime inicio, OffsetDateTime fin) {}
}
