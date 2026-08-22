package ec.edu.ups.reservas.usuarios.api;

import ec.edu.ups.reservas.usuarios.api.UsuarioDtos.*;
import ec.edu.ups.reservas.usuarios.domain.port.in.UsuarioUseCase;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;

    public UsuarioController(UsuarioUseCase usuarioUseCase) {
        this.usuarioUseCase = usuarioUseCase;
    }

    @GetMapping("/me")
    public UsuarioResponse me(@AuthenticationPrincipal Jwt jwt) {
        return usuarioUseCase.obtener(UUID.fromString(jwt.getSubject()));
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioUseCase.listar();
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(@PathVariable UUID id, @RequestBody EstadoRequest request) {
        return usuarioUseCase.cambiarEstado(id, request.activo());
    }
}
