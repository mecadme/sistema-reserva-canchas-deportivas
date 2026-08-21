package ec.edu.ups.reservas.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import ec.edu.ups.reservas.usuarios.api.UsuarioDtos.RegistroRequest;
import ec.edu.ups.reservas.usuarios.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioServiceTest {
    @Test
    void registraUsuarioFinalConPasswordCifrado() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(repository.existsByEmailIgnoreCase("persona@correo.com")).thenReturn(false);
        when(encoder.encode("Segura123*")).thenReturn("hash");
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = new UsuarioService(repository, encoder)
                .registrar(new RegistroRequest("Persona", "persona@correo.com", "Segura123*"));

        assertThat(response.rol()).isEqualTo(Rol.USUARIO);
        assertThat(response.activo()).isTrue();
        verify(encoder).encode("Segura123*");
    }
}
