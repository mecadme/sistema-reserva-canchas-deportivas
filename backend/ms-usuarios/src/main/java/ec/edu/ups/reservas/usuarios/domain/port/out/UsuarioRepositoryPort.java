package ec.edu.ups.reservas.usuarios.domain.port.out;

import ec.edu.ups.reservas.usuarios.domain.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {

    Optional<Usuario> findById(UUID id);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    List<Usuario> findAll();

    Usuario save(Usuario usuario);

    boolean existsByEmailIgnoreCase(String email);
}
