package ec.edu.ups.reservas.reservas.service;

import ec.edu.ups.reservas.reservas.domain.port.in.ReservaUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Job en segundo plano que sostiene RN-08 (todo estado debe reflejar la realidad para
 * fines de trazabilidad y reportes). Sin él, una reserva CONFIRMADA cuya hora ya pasó
 * seguiría contando como "activa" indefinidamente y distorsionaría tanto el límite de
 * reservas simultáneas (RN-06) como los reportes de ocupación de {@code ms-reportes}.
 * {@code fixedDelay} (no {@code fixedRate}) asegura que la siguiente ejecución solo
 * arranque un minuto después de que termine la anterior, evitando solapamientos si la
 * actualización en base de datos tardara más de lo esperado.
 */
@Component
public class FinalizadorJob {

    private final ReservaUseCase reservaUseCase;

    public FinalizadorJob(ReservaUseCase reservaUseCase) {
        this.reservaUseCase = reservaUseCase;
    }

    @Scheduled(fixedDelay = 60_000)
    public void finalizarVencidas() {
        reservaUseCase.finalizarVencidas();
    }
}
