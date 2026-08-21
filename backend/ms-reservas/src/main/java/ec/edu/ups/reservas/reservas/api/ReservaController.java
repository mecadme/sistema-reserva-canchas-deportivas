package ec.edu.ups.reservas.reservas.api;

import ec.edu.ups.reservas.reservas.api.ReservaDtos.*;
import ec.edu.ups.reservas.reservas.domain.port.in.ReservaUseCase;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReservaController {

    private final ReservaUseCase reservaUseCase;

    public ReservaController(ReservaUseCase reservaUseCase) {
        this.reservaUseCase = reservaUseCase;
    }

    @GetMapping("/api/v1/disponibilidad")
    public DisponibilidadResponse disponibilidad(@RequestParam UUID canchaId,
                                                 @RequestParam LocalDate fecha) {
        return reservaUseCase.disponibilidad(canchaId, fecha);
    }

    @PostMapping("/api/v1/reservas")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crear(@AuthenticationPrincipal Jwt jwt,
                                 @Valid @RequestBody CrearReservaRequest request) {
        return reservaUseCase.crear(UUID.fromString(jwt.getSubject()), request);
    }

    @GetMapping("/api/v1/reservas/mias")
    public List<ReservaResponse> mias(@AuthenticationPrincipal Jwt jwt) {
        return reservaUseCase.mias(UUID.fromString(jwt.getSubject()));
    }

    @GetMapping("/api/v1/reservas")
    public List<ReservaResponse> todas() {
        return reservaUseCase.todas();
    }

    @PatchMapping("/api/v1/reservas/{id}/cancelacion")
    public ReservaResponse cancelar(@PathVariable UUID id,
                                    @AuthenticationPrincipal Jwt jwt,
                                    @RequestBody(required = false) CancelarReservaRequest request) {
        boolean esAdmin = jwt.getClaimAsStringList("roles").contains("ADMINISTRADOR");
        String motivo = request == null ? null : request.motivo();
        return reservaUseCase.cancelar(id, UUID.fromString(jwt.getSubject()), esAdmin, motivo);
    }
}
