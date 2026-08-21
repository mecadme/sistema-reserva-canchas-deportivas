package ec.edu.ups.reservas.reportes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import ec.edu.ups.reservas.reportes.api.ReporteDtos.*;
import ec.edu.ups.reservas.reportes.domain.ReporteConsultaRepository;
import ec.edu.ups.reservas.reportes.integration.DatosClient;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class ReporteServiceTest {
    @Test
    void calculaPorcentajeDeOcupacion() {
        DatosClient client = mock(DatosClient.class);
        ReporteConsultaRepository repository = mock(ReporteConsultaRepository.class);
        when(client.resumen(any(), any())).thenReturn(new ResumenReservas(3, 1, 0, 2,
                Map.of("Pádel 1", 3L), Map.of("Pádel 1", 3L), Map.of("PADEL", 3L)));
        when(client.canchas()).thenReturn(List.of(new CanchaInfo(UUID.randomUUID(), "Pádel 1", "PADEL",
                LocalTime.of(7, 0), LocalTime.of(22, 0), true)));

        var report = new ReporteService(client, repository, "America/Guayaquil", 60)
                .generar(UUID.randomUUID(), LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 15));

        assertThat(report.ocupacionPorCancha().get(0).bloquesDisponibles()).isEqualTo(15);
        assertThat(report.ocupacionPorCancha().get(0).porcentajeOcupacion()).isEqualTo(20.0);
        verify(repository).save(any());
    }
}
