package ec.edu.ups.reservas.canchas.api;

import ec.edu.ups.reservas.canchas.api.CanchaDtos.*;
import ec.edu.ups.reservas.canchas.domain.port.in.CanchaUseCase;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/canchas")
public class CanchaController {

    private final CanchaUseCase canchaUseCase;

    public CanchaController(CanchaUseCase canchaUseCase) {
        this.canchaUseCase = canchaUseCase;
    }

    @GetMapping
    public List<CanchaResponse> listar(@RequestParam(defaultValue = "true") boolean soloActivas) {
        return canchaUseCase.listar(soloActivas);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CanchaResponse crear(@Valid @RequestBody CanchaRequest request) {
        return canchaUseCase.crear(request);
    }

    @PutMapping("/{id}")
    public CanchaResponse actualizar(@PathVariable UUID id, @Valid @RequestBody CanchaRequest request) {
        return canchaUseCase.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public CanchaResponse cambiarEstado(@PathVariable UUID id, @RequestBody EstadoRequest request) {
        return canchaUseCase.cambiarEstado(id, request.activa());
    }

    @PostMapping("/{id}/mantenimientos")
    @ResponseStatus(HttpStatus.CREATED)
    public MantenimientoResponse bloquear(@PathVariable UUID id,
                                          @Valid @RequestBody MantenimientoRequest request) {
        return canchaUseCase.bloquear(id, request);
    }

    @GetMapping("/{id}/mantenimientos")
    public List<MantenimientoResponse> mantenimientos(@PathVariable UUID id) {
        return canchaUseCase.mantenimientos(id);
    }
}
