package ec.edu.ups.reservas.usuarios.api;

import ec.edu.ups.reservas.usuarios.domain.Rol;
import ec.edu.ups.reservas.usuarios.domain.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public final class UsuarioDtos {
    private UsuarioDtos() {}

    public record RegistroRequest(
            @NotBlank @Size(max = 120) String nombre,
            @NotBlank @Email @Size(max = 180) String email,
            @NotBlank @Size(min = 8, max = 72) String password) {}

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}

    public record TokenResponse(String tokenAcceso, String tipoToken, long expiraEnSegundos) {}

    public record EstadoRequest(boolean activo) {}

    public record UsuarioResponse(UUID id, String nombre, String email, Rol rol, boolean activo,
                                  OffsetDateTime creadoEn) {
        public static UsuarioResponse from(Usuario usuario) {
            return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(),
                    usuario.getRol(), usuario.isActivo(), usuario.getCreadoEn());
        }
    }
}
