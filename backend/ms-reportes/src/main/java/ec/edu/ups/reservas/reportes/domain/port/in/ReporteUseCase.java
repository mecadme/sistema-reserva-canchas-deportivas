package ec.edu.ups.reservas.reportes.domain.port.in;

import ec.edu.ups.reservas.reportes.api.ReporteDtos.ReporteResponse;
import java.time.LocalDate;
import java.util.UUID;

public interface ReporteUseCase {

    ReporteResponse generar(UUID usuarioId, LocalDate desde, LocalDate hasta);
}
