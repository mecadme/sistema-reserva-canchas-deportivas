package ec.edu.ups.reservas.usuarios.domain.port.in;

import ec.edu.ups.reservas.usuarios.api.UsuarioDtos.*;
import ec.edu.ups.reservas.usuarios.domain.Usuario;

public interface AuthUseCase {

    UsuarioResponse registrar(RegistroRequest request);

    Usuario autenticar(LoginRequest request);
}
