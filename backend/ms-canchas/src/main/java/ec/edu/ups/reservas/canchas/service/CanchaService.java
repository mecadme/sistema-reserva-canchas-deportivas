package ec.edu.ups.reservas.canchas.service;

import ec.edu.ups.reservas.canchas.api.CanchaDtos.*;
import ec.edu.ups.reservas.canchas.domain.*;
import ec.edu.ups.reservas.canchas.domain.port.in.CanchaUseCase;
import ec.edu.ups.reservas.canchas.domain.port.out.BloqueoRepositoryPort;
import ec.edu.ups.reservas.canchas.domain.port.out.CanchaRepositoryPort;
import ec.edu.ups.reservas.canchas.support.ApiException;
import java.time.OffsetDateTime;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CanchaService implements CanchaUseCase {

    private final CanchaRepositoryPort canchas;
    private final BloqueoRepositoryPort bloqueos;

    public CanchaService(CanchaRepositoryPort canchas, BloqueoRepositoryPort bloqueos) {
        this.canchas = canchas;
        this.bloqueos = bloqueos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CanchaResponse> listar(boolean soloActivas) {
        List<Cancha> resultado = soloActivas
                ? canchas.findAllByActivaTrueOrderByNombre()
                : canchas.findAll();
        return resultado.stream().map(CanchaResponse::from).toList();
    }

    @Override
    @Transactional
    public CanchaResponse crear(CanchaRequest request) {
        if (canchas.existsByNombreIgnoreCase(request.nombre())) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe una cancha con ese nombre");
        }
        try {
            Cancha nueva = new Cancha(
                    request.nombre(), request.deporte(),
                    request.horaApertura(), request.horaCierre());
            return CanchaResponse.from(canchas.save(nueva));
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    @Transactional
    public CanchaResponse actualizar(UUID id, CanchaRequest request) {
        Cancha cancha = encontrar(id);
        try {
            cancha.actualizar(request.nombre(), request.deporte(),
                    request.horaApertura(), request.horaCierre());
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return CanchaResponse.from(canchas.save(cancha));
    }

    @Override
    @Transactional
    public CanchaResponse cambiarEstado(UUID id, boolean activa) {
        Cancha cancha = encontrar(id);
        cancha.cambiarEstado(activa);
        return CanchaResponse.from(canchas.save(cancha));
    }

    @Override
    @Transactional
    public MantenimientoResponse bloquear(UUID canchaId, MantenimientoRequest request) {
        encontrar(canchaId);
        try {
            BloqueoMantenimiento bloqueo = new BloqueoMantenimiento(
                    canchaId, request.inicio(), request.fin(), request.motivo());
            return MantenimientoResponse.from(bloqueos.save(bloqueo));
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoResponse> mantenimientos(UUID canchaId) {
        encontrar(canchaId);
        return bloqueos.findByCanchaIdOrderByInicio(canchaId)
                .stream().map(MantenimientoResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CanchaInternaResponse detalleInterno(UUID id, OffsetDateTime inicio, OffsetDateTime fin) {
        Cancha cancha = encontrar(id);
        boolean bloqueada = inicio != null && fin != null
                && bloqueos.existeSolapamiento(id, inicio, fin);
        return new CanchaInternaResponse(
                cancha.getId(), cancha.getNombre(), cancha.getDeporte(),
                cancha.getHoraApertura(), cancha.getHoraCierre(),
                cancha.isActiva(), bloqueada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BloqueoRango> bloqueosDia(UUID canchaId, OffsetDateTime inicio, OffsetDateTime fin) {
        encontrar(canchaId);
        return bloqueos.findSolapamientos(canchaId, inicio, fin)
                .stream()
                .map(b -> new BloqueoRango(b.getInicio(), b.getFin()))
                .toList();
    }

    private Cancha encontrar(UUID id) {
        return canchas.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cancha no encontrada"));
    }
}
