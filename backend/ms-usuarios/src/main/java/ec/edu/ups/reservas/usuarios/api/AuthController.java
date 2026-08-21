package ec.edu.ups.reservas.usuarios.api;

import ec.edu.ups.reservas.usuarios.api.UsuarioDtos.*;
import ec.edu.ups.reservas.usuarios.domain.Usuario;
import ec.edu.ups.reservas.usuarios.domain.port.in.AuthUseCase;
import ec.edu.ups.reservas.usuarios.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final TokenService tokenService;

    public AuthController(AuthUseCase authUseCase, TokenService tokenService) {
        this.authUseCase = authUseCase;
        this.tokenService = tokenService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registro(@Valid @RequestBody RegistroRequest request) {
        return authUseCase.registrar(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = authUseCase.autenticar(request);
        return tokenService.emitir(usuario);
    }
}
