package ec.edu.ups.reservas.canchas.domain;

import jakarta.persistence.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "canchas")
public class Cancha {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Deporte deporte;

    @Column(name = "hora_apertura", nullable = false)
    private LocalTime horaApertura;

    @Column(name = "hora_cierre", nullable = false)
    private LocalTime horaCierre;

    @Column(nullable = false)
    private boolean activa;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    protected Cancha() {}

    public Cancha(String nombre, Deporte deporte, LocalTime apertura, LocalTime cierre) {
        this.id = UUID.randomUUID();
        this.activa = true;
        this.creadoEn = OffsetDateTime.now();
        actualizar(nombre, deporte, apertura, cierre);
    }

    public void actualizar(String nombre, Deporte deporte, LocalTime apertura, LocalTime cierre) {
        if (!apertura.isBefore(cierre)) {
            throw new IllegalArgumentException("La apertura debe ser anterior al cierre");
        }
        this.nombre = nombre.trim();
        this.deporte = deporte;
        this.horaApertura = apertura;
        this.horaCierre = cierre;
        this.actualizadoEn = OffsetDateTime.now();
    }

    public void cambiarEstado(boolean activa) {
        this.activa = activa;
        this.actualizadoEn = OffsetDateTime.now();
    }

    public UUID getId() { return id; }

    public String getNombre() { return nombre; }

    public Deporte getDeporte() { return deporte; }

    public LocalTime getHoraApertura() { return horaApertura; }

    public LocalTime getHoraCierre() { return horaCierre; }

    public boolean isActiva() { return activa; }
}
