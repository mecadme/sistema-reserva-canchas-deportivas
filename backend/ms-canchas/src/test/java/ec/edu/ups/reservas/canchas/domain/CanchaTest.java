package ec.edu.ups.reservas.canchas.domain;

import static org.assertj.core.api.Assertions.*;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class CanchaTest {
    @Test
    void rechazaHorarioInvertido() {
        assertThatThrownBy(() -> new Cancha("Pádel", Deporte.PADEL,
                LocalTime.of(22, 0), LocalTime.of(7, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("apertura");
    }
}
