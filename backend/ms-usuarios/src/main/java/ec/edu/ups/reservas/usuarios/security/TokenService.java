package ec.edu.ups.reservas.usuarios.security;

import ec.edu.ups.reservas.usuarios.api.UsuarioDtos.TokenResponse;
import ec.edu.ups.reservas.usuarios.domain.Usuario;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

/**
 * Único emisor de JWT del sistema (secc. 4.4: autenticación básica con roles, sin OAuth2/SSO).
 * El claim {@code roles} (un solo rol por usuario: ADMINISTRADOR o USUARIO) es lo que los
 * demás microservicios usan como servidores de recursos para decidir autorización de rol
 * (p.ej. {@code hasRole("ADMINISTRADOR")}); todos comparten el mismo secreto HS256
 * ({@code JWT_SECRET}) para poder validar el token sin llamar de vuelta a ms-usuarios.
 */
@Service
public class TokenService {
    private final JwtEncoder encoder;
    private final long expirationMinutes;

    public TokenService(JwtEncoder encoder,
                        @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        this.encoder = encoder;
        this.expirationMinutes = expirationMinutes;
    }

    public TokenResponse emitir(Usuario usuario) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ms-usuarios")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(usuario.getId().toString())
                .claim("email", usuario.getEmail())
                .claim("roles", List.of(usuario.getRol().name()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", expirationMinutes * 60);
    }
}
