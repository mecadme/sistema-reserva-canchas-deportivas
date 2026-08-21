package ec.edu.ups.reservas.reservas.api;

import ec.edu.ups.reservas.reservas.api.ReservaDtos.ResumenReporteResponse;
import ec.edu.ups.reservas.reservas.domain.port.in.ReservaUseCase;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.OffsetDateTime;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/internal/v1/reservas")
public class InternalReservaController {

    private final ReservaUseCase reservaUseCase;

    public InternalReservaController(ReservaUseCase reservaUseCase) {
        this.reservaUseCase = reservaUseCase;
    }

    @GetMapping("/resumen")
    public ResumenReporteResponse resumen(@RequestParam OffsetDateTime desde,
                                          @RequestParam OffsetDateTime hasta) {
        return reservaUseCase.resumen(desde, hasta);
    }
}
