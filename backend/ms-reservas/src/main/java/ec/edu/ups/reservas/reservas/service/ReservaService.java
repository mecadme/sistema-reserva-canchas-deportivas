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

    @Override
    @Transactional
    public ReservaResponse crear(UUID usuarioId, CrearReservaRequest request) {
        OffsetDateTime inicio = request.inicio();
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

    @Override
    @Transactional
    public void finalizarVencidas() {
        repository.finalizarVencidas(OffsetDateTime.now(), EstadoReserva.CONFIRMADA, EstadoReserva.FINALIZADA);
    }

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

    private void validarLimiteReservas(UUID usuarioId) {
        long activas = repository.countByUsuarioIdAndEstadoAndInicioAfter(
                usuarioId, EstadoReserva.CONFIRMADA, OffsetDateTime.now());
        if (activas >= maxActive) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Se alcanzó el límite de reservas activas: " + maxActive);
        }
    }

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
