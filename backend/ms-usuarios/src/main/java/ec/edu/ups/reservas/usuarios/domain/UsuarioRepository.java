package ec.edu.ups.reservas.usuarios.domain;

import ec.edu.ups.reservas.usuarios.domain.port.out.UsuarioRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, UsuarioRepositoryPort {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Override
    Usuario save(Usuario usuario);
}
