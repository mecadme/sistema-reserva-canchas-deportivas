package ec.edu.ups.reservas.reservas.domain;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReservaTest {
    @Test
    void cancelarLiberaElEstadoConfirmado() {
        UUID usuario = UUID.randomUUID();
        Reserva reserva = new Reserva(usuario, UUID.randomUUID(), "Tenis 1", "TENIS",
                OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(1).plusHours(1));

        reserva.cancelar(usuario, "Cambio de planes");

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        assertThat(reserva.getCanceladaPor()).isEqualTo(usuario);
        assertThat(reserva.getMotivoCancelacion()).isEqualTo("Cambio de planes");
    }
}
