package ec.edu.ups.reservas.reservas.service;

import ec.edu.ups.reservas.reservas.domain.port.in.ReservaUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
