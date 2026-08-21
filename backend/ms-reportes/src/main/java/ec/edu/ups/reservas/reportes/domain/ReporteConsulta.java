package ec.edu.ups.reservas.reportes.domain;
import jakarta.persistence.*; import java.time.*; import java.util.UUID;
@Entity @Table(name="reporte_consultas")
public class ReporteConsulta {
 @Id private UUID id; @Column(name="usuario_id",nullable=false)private UUID usuarioId;
 @Column(name="fecha_desde",nullable=false)private LocalDate fechaDesde; @Column(name="fecha_hasta",nullable=false)private LocalDate fechaHasta;
 @Column(name="total_reservas",nullable=false)private long totalReservas; @Column(name="generado_en",nullable=false)private OffsetDateTime generadoEn;
 protected ReporteConsulta(){} public ReporteConsulta(UUID usuarioId,LocalDate desde,LocalDate hasta,long total){this.id=UUID.randomUUID();this.usuarioId=usuarioId;this.fechaDesde=desde;this.fechaHasta=hasta;this.totalReservas=total;this.generadoEn=OffsetDateTime.now();}
}
