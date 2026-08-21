package ec.edu.ups.reservas.usuarios.config;

import ec.edu.ups.reservas.usuarios.support.ApiException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

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
