package ec.edu.ups.reservas.canchas.api;

import ec.edu.ups.reservas.canchas.api.CanchaDtos.*;
import ec.edu.ups.reservas.canchas.domain.port.in.CanchaUseCase;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.OffsetDateTime;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/internal/v1/canchas")
public class InternalCanchaController {

    private final CanchaUseCase canchaUseCase;

    public InternalCanchaController(CanchaUseCase canchaUseCase) {
        this.canchaUseCase = canchaUseCase;
    }

    @GetMapping("/{id}")
    public CanchaInternaResponse obtener(
            @PathVariable UUID id,
            @RequestParam(required = false) OffsetDateTime inicio,
            @RequestParam(required = false) OffsetDateTime fin) {
        return canchaUseCase.detalleInterno(id, inicio, fin);
    }

    @GetMapping
    public List<CanchaResponse> listar() {
        return canchaUseCase.listar(false);
    }

    @GetMapping("/{id}/bloqueos")
    public List<BloqueoRango> bloqueos(
            @PathVariable UUID id,
            @RequestParam OffsetDateTime inicio,
            @RequestParam OffsetDateTime fin) {
        return canchaUseCase.bloqueosDia(id, inicio, fin);
    }
}
