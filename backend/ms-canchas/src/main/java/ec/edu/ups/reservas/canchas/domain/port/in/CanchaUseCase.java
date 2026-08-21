package ec.edu.ups.reservas.canchas.domain.port.in;

import ec.edu.ups.reservas.canchas.api.CanchaDtos.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface CanchaUseCase {

    List<CanchaResponse> listar(boolean soloActivas);

    CanchaResponse crear(CanchaRequest request);

    CanchaResponse actualizar(UUID id, CanchaRequest request);

    CanchaResponse cambiarEstado(UUID id, boolean activa);

    MantenimientoResponse bloquear(UUID canchaId, MantenimientoRequest request);

    List<MantenimientoResponse> mantenimientos(UUID canchaId);

    CanchaInternaResponse detalleInterno(UUID id, OffsetDateTime inicio, OffsetDateTime fin);

    List<BloqueoRango> bloqueosDia(UUID canchaId, OffsetDateTime inicio, OffsetDateTime fin);
}
