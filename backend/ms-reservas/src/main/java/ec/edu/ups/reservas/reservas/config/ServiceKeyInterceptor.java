package ec.edu.ups.reservas.reservas.config;

import ec.edu.ups.reservas.reservas.support.ApiException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Guarda de las rutas {@code /internal/**} usadas para comunicación entre microservicios
 * (secc. 4.2: "La comunicación entre microservicios... se realiza vía HTTP/REST de forma
 * síncrona"). Estas rutas están excluidas del filtro JWT en {@code SecurityConfig} porque no
 * hay un usuario autenticado detrás de una llamada de {@code ms-reservas} a {@code ms-canchas};
 * en su lugar se exige un secreto compartido ({@code SERVICE_API_KEY}) enviado en el header
 * {@code X-Service-Key} por el cliente REST del servicio llamante (ver {@code CanchaClient}).
 */
@Component
public class ServiceKeyInterceptor implements HandlerInterceptor {

    private final String expectedKey;

    public ServiceKeyInterceptor(@Value("${security.service-api-key}") String expectedKey) {
        this.expectedKey = expectedKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String suppliedKey = request.getHeader("X-Service-Key");
        if (!expectedKey.equals(suppliedKey)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Clave de servicio inválida");
        }
        return true;
    }
}
