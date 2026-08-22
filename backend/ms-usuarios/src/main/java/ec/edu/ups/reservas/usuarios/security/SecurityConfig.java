package ec.edu.ups.reservas.usuarios.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        SecretKey key = secretKey(secret);
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtEncoder jwtEncoder(@Value("${security.jwt.secret}") String secret) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(secret)));
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4300"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            JwtAuthenticationConverter converter) throws Exception {
        return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                        // /internal/** queda abierto a nivel de Spring Security (sin JWT) porque
                        // las llamadas servicio-a-servicio (secc. 4.2) no tienen un usuario final
                        // detrás; en su lugar, ServiceKeyInterceptor exige el header X-Service-Key
                        // antes de que la petición llegue al controlador. No exponer /internal/**
                        // a través del API Gateway/BFF público es responsabilidad de la capa de red.
                        .requestMatchers("/internal/**").permitAll()
                        .requestMatchers("/api/v1/usuarios/me").authenticated()
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .build();
    }

    private SecretKey secretKey(String secret) {
        if (secret.length() < 32) throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
