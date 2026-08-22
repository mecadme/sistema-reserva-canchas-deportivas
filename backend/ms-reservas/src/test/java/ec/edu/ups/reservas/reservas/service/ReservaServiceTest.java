package ec.edu.ups.reservas.reservas.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ec.edu.ups.reservas.reservas.api.ReservaDtos.CrearReservaRequest;
import ec.edu.ups.reservas.reservas.domain.port.out.CanchaServicePort;
import ec.edu.ups.reservas.reservas.domain.port.out.CanchaServicePort.CanchaDetalle;
import ec.edu.ups.reservas.reservas.domain.port.out.ReservaRepositoryPort;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReservaServiceTest {

    private final ReservaRepositoryPort repository = mock(ReservaRepositoryPort.class);
    private final CanchaServicePort canchas = mock(CanchaServicePort.class);
    private final ReservaService service =
            new ReservaService(repository, canchas, 60, 3, "America/Guayaquil");

    /**
     * Regresión: {@code validarBloque}/{@code validarCancha} comparaban "mismo día" y
     * "horario de atención" sobre el offset tal cual llegaba del request. Como Jackson
     * normaliza {@code OffsetDateTime} a UTC al deserializar, un bloque de 19:00-20:00 hora
     * Guayaquil (offset -05:00) cruza medianoche en UTC y era rechazado con 400 aunque en
     * hora local sigue siendo el mismo día.
     */
    @Test
    void creaUnaReservaDeNocheQueCruzaMedianocheEnUtc() {
        UUID usuarioId = UUID.randomUUID();
        UUID canchaId = UUID.randomUUID();
        ZoneId zona = ZoneId.of("America/Guayaquil");
        OffsetDateTime inicioLocal = OffsetDateTime.now(zona)
                .plusDays(1)
                .withHour(19)
                .truncatedTo(ChronoUnit.HOURS);

        when(canchas.obtener(any(), any(), any())).thenReturn(
                new CanchaDetalle(canchaId, "Pádel A", "PADEL",
                        LocalTime.of(7, 0), LocalTime.of(22, 0), true, false));
        when(repository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repository.countByUsuarioIdAndEstadoAndInicioAfter(any(), any(), any())).thenReturn(0L);

        assertThatCode(() -> service.crear(usuarioId, new CrearReservaRequest(canchaId, inicioLocal)))
                .doesNotThrowAnyException();
    }

    /**
     * El mismo instante, expresado con offset UTC (como llegaría tras la normalización de
     * Jackson) debe comportarse igual: la validación no puede depender del offset que mande
     * el cliente.
     */
    @Test
    void creaLaMismaReservaSinImportarElOffsetDelRequest() {
        UUID usuarioId = UUID.randomUUID();
        UUID canchaId = UUID.randomUUID();
        ZoneId zona = ZoneId.of("America/Guayaquil");
        OffsetDateTime inicioLocal = OffsetDateTime.now(zona)
                .plusDays(1)
                .withHour(19)
                .truncatedTo(ChronoUnit.HOURS);
        OffsetDateTime inicioUtc = inicioLocal.withOffsetSameInstant(ZoneOffset.UTC);

        when(canchas.obtener(any(), any(), any())).thenReturn(
                new CanchaDetalle(canchaId, "Pádel A", "PADEL",
                        LocalTime.of(7, 0), LocalTime.of(22, 0), true, false));
        when(repository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));
        when(repository.countByUsuarioIdAndEstadoAndInicioAfter(any(), any(), any())).thenReturn(0L);

        assertThatCode(() -> service.crear(usuarioId, new CrearReservaRequest(canchaId, inicioUtc)))
                .doesNotThrowAnyException();
    }
}
