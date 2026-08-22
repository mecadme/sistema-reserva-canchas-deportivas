package ec.edu.ups.reservas.canchas.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloqueos_mantenimiento")
public class BloqueoMantenimiento {
    @Id private UUID id;
    @Column(name = "cancha_id", nullable = false) private UUID canchaId;
    @Column(nullable = false) private OffsetDateTime inicio;
    @Column(nullable = false) private OffsetDateTime fin;
    @Column(nullable = false, length = 250) private String motivo;
    @Column(name = "creado_en", nullable = false) private OffsetDateTime creadoEn;
    protected BloqueoMantenimiento() {}
    public BloqueoMantenimiento(UUID canchaId, OffsetDateTime inicio, OffsetDateTime fin, String motivo) {
        if (!inicio.isBefore(fin)) throw new IllegalArgumentException("El inicio debe ser anterior al fin");
        this.id = UUID.randomUUID(); this.canchaId = canchaId; this.inicio = inicio; this.fin = fin;
        this.motivo = motivo.trim(); this.creadoEn = OffsetDateTime.now();
    }
    public UUID getId() { return id; } public UUID getCanchaId() { return canchaId; }
    public OffsetDateTime getInicio() { return inicio; } public OffsetDateTime getFin() { return fin; }
    public String getMotivo() { return motivo; }
}
