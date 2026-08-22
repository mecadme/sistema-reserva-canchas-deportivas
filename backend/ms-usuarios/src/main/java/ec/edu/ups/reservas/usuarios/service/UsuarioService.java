package ec.edu.ups.reservas.usuarios.service;

import ec.edu.ups.reservas.usuarios.api.UsuarioDtos.*;
import ec.edu.ups.reservas.usuarios.domain.*;
import ec.edu.ups.reservas.usuarios.domain.port.in.AuthUseCase;
import ec.edu.ups.reservas.usuarios.domain.port.in.UsuarioUseCase;
import ec.edu.ups.reservas.usuarios.domain.port.out.UsuarioRepositoryPort;
import ec.edu.ups.reservas.usuarios.support.ApiException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService implements AuthUseCase, UsuarioUseCase {

    private final UsuarioRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepositoryPort repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Usuario usuario = new Usuario(request.nombre(), request.email(),
                passwordEncoder.encode(request.password()), Rol.USUARIO);
        return UsuarioResponse.from(repository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario autenticar(LoginRequest request) {
        Usuario usuario = repository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
        if (!usuario.isActivo() || !passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtener(UUID id) {
        return UsuarioResponse.from(encontrar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarEstado(UUID id, boolean activo) {
        Usuario usuario = encontrar(id);
        usuario.cambiarEstado(activo);
        return UsuarioResponse.from(repository.save(usuario));
    }

    private Usuario encontrar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
