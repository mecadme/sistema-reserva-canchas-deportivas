package ec.edu.ups.reservas.reservas.integration;

import ec.edu.ups.reservas.reservas.domain.port.out.CanchaServicePort;
import ec.edu.ups.reservas.reservas.support.ApiException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class CanchaClient implements CanchaServicePort {

    private final RestClient client;
    private final String serviceKey;

    public CanchaClient(@Value("${services.canchas-url}") String baseUrl,
                        @Value("${security.service-api-key}") String serviceKey) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
        this.serviceKey = serviceKey;
    }

    @Override
    public CanchaDetalle obtener(UUID id, OffsetDateTime inicio, OffsetDateTime fin) {
        try {
            return client.get()
                    .uri(uri -> uri.path("/internal/v1/canchas/{id}")
                            .queryParam("inicio", inicio)
                            .queryParam("fin", fin)
                            .build(id))
                    .header("X-Service-Key", serviceKey)
                    .retrieve()
                    .body(CanchaDetalle.class);
        } catch (RestClientResponseException ex) {
            HttpStatus status = ex.getStatusCode().value() == 404
                    ? HttpStatus.NOT_FOUND : HttpStatus.BAD_GATEWAY;
            throw new ApiException(status, "No fue posible validar la cancha");
        } catch (RestClientException ex) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de canchas no está disponible");
        }
    }

    @Override
    public List<RangoBloqueo> bloqueos(UUID canchaId, OffsetDateTime inicio, OffsetDateTime fin) {
        try {
            return client.get()
                    .uri(uri -> uri.path("/internal/v1/canchas/{id}/bloqueos")
                            .queryParam("inicio", inicio)
                            .queryParam("fin", fin)
                            .build(canchaId))
                    .header("X-Service-Key", serviceKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RangoBloqueo>>() {});
        } catch (RestClientException ex) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de canchas no está disponible");
        }
    }
}
