package ec.edu.ups.reservas.reservas.service;

import ec.edu.ups.reservas.reservas.api.ReservaDtos.*;
import ec.edu.ups.reservas.reservas.domain.*;
import ec.edu.ups.reservas.reservas.domain.port.in.ReservaUseCase;
import ec.edu.ups.reservas.reservas.domain.port.out.CanchaServicePort;
import ec.edu.ups.reservas.reservas.domain.port.out.CanchaServicePort.CanchaDetalle;
import ec.edu.ups.reservas.reservas.domain.port.out.CanchaServicePort.RangoBloqueo;
import ec.edu.ups.reservas.reservas.domain.port.out.ReservaRepositoryPort;
import ec.edu.ups.reservas.reservas.support.ApiException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementa el proceso de reserva descrito en el Alcance Funcional (secc. 3.3.2/3.3.3)
 * y las reglas de negocio RN-01 a RN-08. Es el único punto donde se decide si un bloque
 * horario puede reservarse o cancelarse; {@code ms-canchas} solo aporta los datos de la
 * cancha (horario de atención, estado, bloqueos de mantenimiento) vía {@link CanchaServicePort}.
 */
@Service
public class ReservaService implements ReservaUseCase {

    private final ReservaRepositoryPort repository;
    private final CanchaServicePort canchas;
    private final int slotMinutes;
    private final int maxActive;
    private final ZoneId zoneId;

    public ReservaService(ReservaRepositoryPort repository,
                          CanchaServicePort canchas,
                          @Value("${business.slot-minutes}") int slotMinutes,
                          @Value("${business.max-active-reservations}") int maxActive,
                          @Value("${business.zone-id}") String zoneId) {
        this.repository = repository;
        this.canchas = canchas;
        this.slotMinutes = slotMinutes;
        this.maxActive = maxActive;
        this.zoneId = ZoneId.of(zoneId);
    }

    /**
     * Crea una reserva (RN-01: cancha + fecha + bloque fijo de {@code slotMinutes}).
     * <p>
     * La validación de solapamiento (RN-02) es de doble capa, a propósito:
     * 1) aquí arriba no se hace ningún "SELECT ocupados" porque eso sería vulnerable a
     *    condiciones de carrera entre dos solicitudes concurrentes sobre el mismo slot;
     * 2) la garantía real la da un índice único {@code (cancha_id, inicio)} en la tabla
     *    {@code reservas} (ver migración Flyway), y aquí solo se atrapa el conflicto que
     *    Postgres reporta como {@link DataIntegrityViolationException} y se traduce a 409.
     * Por eso el nombre y el deporte de la cancha se copian ("snapshot") dentro de la
     * reserva: si luego un admin renombra o inactiva la cancha, el historial y los
     * reportes no cambian retroactivamente.
     */
    @Override
    @Transactional
    public ReservaResponse crear(UUID usuarioId, CrearReservaRequest request) {
        OffsetDateTime inicio = request.inicio().atZoneSameInstant(zoneId).toOffsetDateTime();
        OffsetDateTime fin = inicio.plusMinutes(slotMinutes);
        validarBloque(inicio, fin);
        validarLimiteReservas(usuarioId);
        CanchaDetalle cancha = canchas.obtener(request.canchaId(), inicio, fin);
        validarCancha(cancha, inicio, fin);
        try {
            Reserva nueva = new Reserva(
                    usuarioId, cancha.id(), cancha.nombre(), cancha.deporte(), inicio, fin);
            return ReservaResponse.from(repository.saveAndFlush(nueva));
        } catch (DataIntegrityViolationException ex) {
            throw new ApiException(HttpStatus.CONFLICT, "El bloque horario ya está ocupado");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> mias(UUID usuarioId) {
        return repository.findByUsuarioIdOrderByInicioDesc(usuarioId)
                .stream().map(ReservaResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> todas() {
        return repository.findAll().stream().map(ReservaResponse::from).toList();
    }

    /**
     * Cancela una reserva (RN-03: dueño o administrador; RN-04: solo si aún no inició).
     * RN-05 ("toda cancelación libera el bloque automáticamente") no requiere lógica
     * adicional aquí: {@link #construirSlots} y el conteo de {@link #disponibilidad}
     * solo consideran reservas en estado {@code CONFIRMADA}, así que en cuanto el estado
     * cambia a {@code CANCELADA} el bloque vuelve a verse disponible en la siguiente consulta.
     */
    @Override
    @Transactional
    public ReservaResponse cancelar(UUID id, UUID actorId, boolean administrador, String motivo) {
        Reserva reserva = encontrar(id);
        if (!administrador && !reserva.getUsuarioId().equals(actorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Solo puede cancelar sus propias reservas");
        }
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new ApiException(HttpStatus.CONFLICT, "La reserva no está confirmada");
        }
        if (!OffsetDateTime.now().isBefore(reserva.getInicio())) {
            throw new ApiException(HttpStatus.CONFLICT, "No se puede cancelar una reserva iniciada o pasada");
        }
        reserva.cancelar(actorId, motivo == null ? null : motivo.trim());
        return ReservaResponse.from(repository.save(reserva));
    }

    /**
     * Arma la grilla de disponibilidad de la pantalla "Consulta de disponibilidad" (secc. 3.3.1).
     * {@code inicioDia}/{@code finDia} se calculan en la zona horaria del negocio
     * ({@code business.zone-id}, por defecto America/Guayaquil) y luego se convierten a
     * {@link OffsetDateTime} para poder compararlos contra columnas timestamptz sin ambigüedad
     * de huso horario, sin importar en qué zona corra el servidor.
     */
    @Override
    @Transactional(readOnly = true)
    public DisponibilidadResponse disponibilidad(UUID canchaId, LocalDate fecha) {
        ZoneOffset offset = zoneId.getRules().getOffset(fecha.atStartOfDay(zoneId).toInstant());
        OffsetDateTime inicioDia = fecha.atStartOfDay().atOffset(offset);
        OffsetDateTime finDia = inicioDia.plusDays(1);

        CanchaDetalle cancha = canchas.obtener(canchaId, inicioDia, inicioDia.plusMinutes(1));
        List<RangoBloqueo> bloqueosDia = canchas.bloqueos(canchaId, inicioDia, finDia);
        List<Reserva> reservasConfirmadas = repository.findByCanchaIdAndEstadoAndInicioLessThanAndFinGreaterThan(
                canchaId, EstadoReserva.CONFIRMADA, finDia, inicioDia);

        List<SlotResponse> slots = construirSlots(cancha, bloqueosDia, reservasConfirmadas, fecha, offset);
        return new DisponibilidadResponse(canchaId, fecha, slots);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenReporteResponse resumen(OffsetDateTime desde, OffsetDateTime hasta) {
        List<Reserva> reservas = repository
                .findByInicioGreaterThanEqualAndInicioLessThanOrderByInicio(desde, hasta);
        Map<String, Long> totalPorCancha = reservas.stream()
                .collect(Collectors.groupingBy(Reserva::getCanchaNombre, Collectors.counting()));
        Map<String, Long> ocupadasPorCancha = reservas.stream()
                .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
                .collect(Collectors.groupingBy(Reserva::getCanchaNombre, Collectors.counting()));
        Map<String, Long> porDeporte = reservas.stream()
                .collect(Collectors.groupingBy(Reserva::getDeporte, Collectors.counting()));
        return new ResumenReporteResponse(
                reservas.size(),
                contar(reservas, EstadoReserva.CONFIRMADA),
                contar(reservas, EstadoReserva.CANCELADA),
                contar(reservas, EstadoReserva.FINALIZADA),
                totalPorCancha,
                ocupadasPorCancha,
                porDeporte);
    }

    /**
     * RN-08: mueve a FINALIZADA toda reserva CONFIRMADA cuya hora de inicio ya pasó, para que
     * el estado sea trazable en reportes. Se invoca cada minuto desde {@link FinalizadorJob};
     * es una actualización masiva vía JPQL (no carga entidades) porque el volumen esperado no
     * justifica traer filas a memoria solo para cambiarles el estado.
     */
    @Override
    @Transactional
    public void finalizarVencidas() {
        repository.finalizarVencidas(OffsetDateTime.now(), EstadoReserva.CONFIRMADA, EstadoReserva.FINALIZADA);
    }

    /**
     * Genera los bloques de {@code slotMinutes} entre la apertura y el cierre de la cancha
     * para un día, marcando cada uno como DISPONIBLE, OCUPADO (reserva CONFIRMADA solapada)
     * o BLOQUEADO (cancha inactiva o con mantenimiento solapado, secc. 3.3.4/RN-07).
     * La condición {@code r.inicio < slotFin && r.fin > slotInicio} es la prueba estándar de
     * solapamiento de intervalos semiabiertos [inicio, fin); se reutiliza igual para los
     * bloqueos de mantenimiento. BLOQUEADO tiene prioridad sobre OCUPADO porque una cancha
     * inactiva/en mantenimiento nunca debe ofrecerse como reservable aunque no tenga reservas.
     */
    private List<SlotResponse> construirSlots(CanchaDetalle cancha,
                                               List<RangoBloqueo> bloqueosDia,
                                               List<Reserva> reservasConfirmadas,
                                               LocalDate fecha,
                                               ZoneOffset offset) {
        OffsetDateTime cursor = fecha.atTime(cancha.horaApertura()).atOffset(offset);
        OffsetDateTime cierre = fecha.atTime(cancha.horaCierre()).atOffset(offset);
        List<SlotResponse> slots = new ArrayList<>();
        while (!cursor.plusMinutes(slotMinutes).isAfter(cierre)) {
            OffsetDateTime slotInicio = cursor;
            OffsetDateTime slotFin = cursor.plusMinutes(slotMinutes);
            boolean ocupada = reservasConfirmadas.stream()
                    .anyMatch(r -> r.getInicio().isBefore(slotFin) && r.getFin().isAfter(slotInicio));
            boolean bloqueada = !cancha.activa() || bloqueosDia.stream()
                    .anyMatch(b -> b.inicio().isBefore(slotFin) && b.fin().isAfter(slotInicio));
            slots.add(new SlotResponse(slotInicio, slotFin,
                    bloqueada ? "BLOQUEADO" : ocupada ? "OCUPADO" : "DISPONIBLE"));
            cursor = slotFin;
        }
        return slots;
    }

    /** RN-06: tope de reservas CONFIRMADAS y futuras por usuario ({@code business.max-active-reservations}). */
    private void validarLimiteReservas(UUID usuarioId) {
        long activas = repository.countByUsuarioIdAndEstadoAndInicioAfter(
                usuarioId, EstadoReserva.CONFIRMADA, OffsetDateTime.now());
        if (activas >= maxActive) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Se alcanzó el límite de reservas activas: " + maxActive);
        }
    }

    /**
     * RN-01: el bloque debe iniciar en el futuro, en una hora exacta (alineado a la grilla
     * de {@link #construirSlots}) y no cruzar la medianoche, para que "fecha" siga siendo
     * un concepto de un solo día tanto en la reserva como en los reportes de ocupación.
     * <p>
     * {@code inicio}/{@code fin} ya llegan normalizados a {@code zoneId} (ver {@link #crear}),
     * así que "mismo día" se evalúa en la zona horaria del negocio y no en el offset que
     * mande el cliente ni en UTC.
     */
    private void validarBloque(OffsetDateTime inicio, OffsetDateTime fin) {
        if (!inicio.isAfter(OffsetDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La reserva debe iniciar en el futuro");
        }
        if (inicio.getMinute() != 0 || inicio.getSecond() != 0 || inicio.getNano() != 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La reserva debe iniciar en una hora exacta");
        }
        if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La reserva debe finalizar el mismo día");
        }
    }

    /**
     * Repite del lado de {@code ms-reservas} las condiciones que definen si una cancha es
     * reservable (activa, sin bloqueo de mantenimiento vigente RN-07, dentro del horario de
     * atención). Es necesario porque, al ser microservicios independientes con bases de datos
     * separadas (secc. 4.2/4.3), {@code ms-reservas} no puede consultar directamente las
     * tablas de {@code canchas_db}: solo confía en el snapshot que devuelve {@link CanchaServicePort}.
     */
    private void validarCancha(CanchaDetalle cancha, OffsetDateTime inicio, OffsetDateTime fin) {
        if (!cancha.activa()) {
            throw new ApiException(HttpStatus.CONFLICT, "La cancha está inactiva");
        }
        if (cancha.bloqueada()) {
            throw new ApiException(HttpStatus.CONFLICT, "La cancha está bloqueada por mantenimiento");
        }
        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFin = fin.toLocalTime();
        if (horaInicio.isBefore(cancha.horaApertura()) || horaFin.isAfter(cancha.horaCierre())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El bloque está fuera del horario de atención");
        }
    }

    private long contar(List<Reserva> reservas, EstadoReserva estado) {
        return reservas.stream().filter(r -> r.getEstado() == estado).count();
    }

    private Reserva encontrar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));
    }
}
