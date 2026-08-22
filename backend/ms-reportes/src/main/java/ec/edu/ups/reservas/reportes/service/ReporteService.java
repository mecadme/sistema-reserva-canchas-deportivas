package ec.edu.ups.reservas.reportes.service;

import ec.edu.ups.reservas.reportes.api.ReporteDtos.*;
import ec.edu.ups.reservas.reportes.domain.*;
import ec.edu.ups.reservas.reportes.domain.port.in.ReporteUseCase;
import ec.edu.ups.reservas.reportes.domain.port.out.DatosServicePort;
import ec.edu.ups.reservas.reportes.domain.port.out.ReporteConsultaRepositoryPort;
import ec.edu.ups.reservas.reportes.support.ApiException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Módulo de reportes de solo lectura (secc. 3.3.5), único consumidor del rol ADMINISTRADOR.
 * No tiene tablas propias de canchas ni de reservas: agrega, en el momento de la consulta,
 * datos que pide vía HTTP a {@code ms-reservas} y {@code ms-canchas} ({@link DatosServicePort}),
 * respetando la independencia de datos entre microservicios (secc. 4.3). Lo único que persiste
 * localmente es un log de auditoría de qué reporte se generó y cuándo ({@link ReporteConsulta}).
 */
@Service
public class ReporteService implements ReporteUseCase {

    private final DatosServicePort client;
    private final ReporteConsultaRepositoryPort repository;
    private final ZoneId zoneId;
    private final int slotMinutes;

    public ReporteService(DatosServicePort client,
                          ReporteConsultaRepositoryPort repository,
                          @Value("${business.zone-id}") String zoneId,
                          @Value("${business.slot-minutes}") int slotMinutes) {
        this.client = client;
        this.repository = repository;
        this.zoneId = ZoneId.of(zoneId);
        this.slotMinutes = slotMinutes;
    }

    @Override
    @Transactional
    public ReporteResponse generar(UUID usuarioId, LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        OffsetDateTime inicio = desde.atStartOfDay(zoneId).toOffsetDateTime();
        OffsetDateTime fin = hasta.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime();
        ResumenReservas resumen = client.resumen(inicio, fin);
        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;
        List<OcupacionCancha> ocupacion = calcularOcupacionPorCancha(resumen, dias);
        registrarConsulta(usuarioId, desde, hasta, resumen.total());
        return new ReporteResponse(
                desde, hasta,
                resumen.total(), resumen.confirmadas(), resumen.canceladas(), resumen.finalizadas(),
                ocupacion, resumen.porDeporte());
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "La fecha desde no puede ser posterior a la fecha hasta");
        }
    }

    private List<OcupacionCancha> calcularOcupacionPorCancha(ResumenReservas resumen, long dias) {
        return client.canchas().stream()
                .map(cancha -> calcularOcupacion(cancha, resumen, dias))
                .sorted(Comparator.comparingDouble(OcupacionCancha::porcentajeOcupacion).reversed())
                .toList();
    }

    /**
     * "Porcentaje de ocupación por cancha (horas reservadas sobre horas disponibles)" (secc. 3.3.5).
     * {@code bloquesDisponibles} es una capacidad teórica: (minutos de horario de atención /
     * duración del slot) * días del rango; no descuenta mantenimientos, así que una cancha con
     * bloqueos frecuentes puede mostrar una ocupación algo más baja que la real. Una cancha
     * inactiva se reporta con 0 bloques disponibles (evita división por cero) y por lo tanto
     * 0% de ocupación en vez de "sin datos". El {@code Math.round(... * 10_000.0) / 100.0}
     * redondea a 2 decimales sin arrastrar errores de punto flotante de una división directa.
     */
    private OcupacionCancha calcularOcupacion(CanchaInfo cancha, ResumenReservas resumen, long dias) {
        long minutosHorario = Duration.between(cancha.horaApertura(), cancha.horaCierre()).toMinutes();
        long bloquesDisponibles = cancha.activa() ? (minutosHorario / slotMinutes) * dias : 0;
        long reservasOcupadas = resumen.ocupadasPorCancha().getOrDefault(cancha.nombre(), 0L);
        double porcentaje = bloquesDisponibles == 0
                ? 0.0
                : Math.round((reservasOcupadas * 10_000.0 / bloquesDisponibles)) / 100.0;
        return new OcupacionCancha(cancha.nombre(), reservasOcupadas, bloquesDisponibles, porcentaje);
    }

    private void registrarConsulta(UUID usuarioId, LocalDate desde, LocalDate hasta, long total) {
        repository.save(new ReporteConsulta(usuarioId, desde, hasta, total));
    }
}
