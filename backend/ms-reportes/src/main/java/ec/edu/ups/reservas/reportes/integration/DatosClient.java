package ec.edu.ups.reservas.reportes.integration;

import ec.edu.ups.reservas.reportes.api.ReporteDtos.*;
import ec.edu.ups.reservas.reportes.domain.port.out.DatosServicePort;
import ec.edu.ups.reservas.reportes.support.ApiException;
import java.time.OffsetDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class DatosClient implements DatosServicePort {

    private final RestClient reservas;
    private final RestClient canchas;
    private final String serviceKey;

    public DatosClient(@Value("${services.reservas-url}") String reservasUrl,
                       @Value("${services.canchas-url}") String canchasUrl,
                       @Value("${security.service-api-key}") String serviceKey) {
        this.reservas = RestClient.builder().baseUrl(reservasUrl).build();
        this.canchas = RestClient.builder().baseUrl(canchasUrl).build();
        this.serviceKey = serviceKey;
    }

    @Override
    public ResumenReservas resumen(OffsetDateTime desde, OffsetDateTime hasta) {
        try {
            return reservas.get()
                    .uri(u -> u.path("/internal/v1/reservas/resumen")
                            .queryParam("desde", desde)
                            .queryParam("hasta", hasta)
                            .build())
                    .header("X-Service-Key", serviceKey)
                    .retrieve()
                    .body(ResumenReservas.class);
        } catch (RestClientException ex) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "No fue posible obtener datos de reservas");
        }
    }

    @Override
    public List<CanchaInfo> canchas() {
        try {
            return canchas.get()
                    .uri("/internal/v1/canchas")
                    .header("X-Service-Key", serviceKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CanchaInfo>>() {});
        } catch (RestClientException ex) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "No fue posible obtener datos de canchas");
        }
    }
}
