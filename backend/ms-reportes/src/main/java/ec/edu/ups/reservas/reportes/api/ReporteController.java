package ec.edu.ups.reservas.reportes.api;

import ec.edu.ups.reservas.reportes.api.ReporteDtos.ReporteResponse;
import ec.edu.ups.reservas.reportes.domain.port.in.ReporteUseCase;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    private final ReporteUseCase reporteUseCase;

    public ReporteController(ReporteUseCase reporteUseCase) {
        this.reporteUseCase = reporteUseCase;
    }

    @GetMapping("/ocupacion")
    public ReporteResponse ocupacion(@RequestParam LocalDate desde,
                                     @RequestParam LocalDate hasta,
                                     @AuthenticationPrincipal Jwt jwt) {
        return reporteUseCase.generar(UUID.fromString(jwt.getSubject()), desde, hasta);
    }
}
