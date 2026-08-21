package ec.edu.ups.reservas.usuarios.domain.port.in;

import ec.edu.ups.reservas.usuarios.api.UsuarioDtos.*;
import java.util.List;
import java.util.UUID;

public interface UsuarioUseCase {

    UsuarioResponse obtener(UUID id);

    List<UsuarioResponse> listar();

    UsuarioResponse cambiarEstado(UUID id, boolean activo);
}
