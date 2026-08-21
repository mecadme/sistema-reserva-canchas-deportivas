package ec.edu.ups.reservas.reportes.api;

import java.time.*;
import java.util.*;

public final class ReporteDtos {

    private ReporteDtos() {}

    public record OcupacionCancha(String cancha, long reservasOcupadas,
                                   long bloquesDisponibles, double porcentajeOcupacion) {}

    public record ReporteResponse(LocalDate desde, LocalDate hasta,
                                   long totalReservas, long confirmadas, long canceladas, long finalizadas,
                                   List<OcupacionCancha> ocupacionPorCancha,
                                   Map<String, Long> reservasPorDeporte) {}

    public record ResumenReservas(long total, long confirmadas, long canceladas, long finalizadas,
                                   Map<String, Long> porCancha,
                                   Map<String, Long> ocupadasPorCancha,
                                   Map<String, Long> porDeporte) {}

    public record CanchaInfo(UUID id, String nombre, String deporte,
                              LocalTime horaApertura, LocalTime horaCierre, boolean activa) {}
}
