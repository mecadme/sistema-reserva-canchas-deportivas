package ec.edu.ups.reservas.usuarios.api;

import ec.edu.ups.reservas.usuarios.api.UsuarioDtos.UsuarioResponse;
import ec.edu.ups.reservas.usuarios.domain.port.in.UsuarioUseCase;
import io.swagger.v3.oas.annotations.Hidden;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/internal/v1/usuarios")
public class InternalUsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    public InternalUsuarioController(UsuarioUseCase usuarioUseCase) {
        this.usuarioUseCase = usuarioUseCase;
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable UUID id) {
        return usuarioUseCase.obtener(id);
    }
}
