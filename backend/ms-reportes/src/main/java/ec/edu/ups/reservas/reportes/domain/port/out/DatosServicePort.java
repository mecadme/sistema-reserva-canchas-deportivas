package ec.edu.ups.reservas.reportes.domain.port.out;

import ec.edu.ups.reservas.reportes.api.ReporteDtos.*;
import java.time.OffsetDateTime;
import java.util.List;

public interface DatosServicePort {

    ResumenReservas resumen(OffsetDateTime desde, OffsetDateTime hasta);

    List<CanchaInfo> canchas();
}
