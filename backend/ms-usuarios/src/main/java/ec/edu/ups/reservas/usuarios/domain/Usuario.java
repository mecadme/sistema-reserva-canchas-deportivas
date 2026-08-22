package ec.edu.ups.reservas.usuarios.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    private UUID id;
    @Column(nullable = false, length = 120)
    private String nombre;
    @Column(nullable = false, unique = true, length = 180)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Rol rol;
    @Column(nullable = false)
    private boolean activo;
    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;
    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    protected Usuario() {}

    public Usuario(String nombre, String email, String passwordHash, Rol rol) {
        this.id = UUID.randomUUID();
        this.nombre = nombre.trim();
        this.email = email.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = true;
        this.creadoEn = OffsetDateTime.now();
        this.actualizadoEn = this.creadoEn;
    }

    public void cambiarEstado(boolean activo) {
        this.activo = activo;
        this.actualizadoEn = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Rol getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
}
