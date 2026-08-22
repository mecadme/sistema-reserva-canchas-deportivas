package ec.edu.ups.reservas.reservas.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "cancha_id", nullable = false)
    private UUID canchaId;

    @Column(name = "cancha_nombre", nullable = false, length = 120)
    private String canchaNombre;

    @Column(nullable = false, length = 20)
    private String deporte;

    @Column(nullable = false)
    private OffsetDateTime inicio;

    @Column(nullable = false)
    private OffsetDateTime fin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    @Column(name = "cancelada_por")
    private UUID canceladaPor;

    @Column(name = "motivo_cancelacion", length = 250)
    private String motivoCancelacion;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    protected Reserva() {}

    public Reserva(UUID usuarioId, UUID canchaId, String canchaNombre, String deporte,
                   OffsetDateTime inicio, OffsetDateTime fin) {
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.canchaId = canchaId;
        this.canchaNombre = canchaNombre;
        this.deporte = deporte;
        this.inicio = inicio;
        this.fin = fin;
        this.estado = EstadoReserva.CONFIRMADA;
        this.creadoEn = OffsetDateTime.now();
        this.actualizadoEn = this.creadoEn;
    }

    /**
     * RN-05: cancelar solo cambia el estado (no borra la fila ni el rango horario), por lo
     * que el bloque queda libre implícitamente en cuanto deja de estar en CONFIRMADA —
     * ver {@code ReservaService#construirSlots}, que filtra por ese estado.
     */
    public void cancelar(UUID actorId, String motivo) {
        this.estado = EstadoReserva.CANCELADA;
        this.canceladaPor = actorId;
        this.motivoCancelacion = motivo;
        this.actualizadoEn = OffsetDateTime.now();
    }

    public UUID getId() { return id; }

    public UUID getUsuarioId() { return usuarioId; }

    public UUID getCanchaId() { return canchaId; }

    public String getCanchaNombre() { return canchaNombre; }

    public String getDeporte() { return deporte; }

    public OffsetDateTime getInicio() { return inicio; }

    public OffsetDateTime getFin() { return fin; }

    public EstadoReserva getEstado() { return estado; }

    public UUID getCanceladaPor() { return canceladaPor; }

    public String getMotivoCancelacion() { return motivoCancelacion; }

    public OffsetDateTime getCreadoEn() { return creadoEn; }
}
