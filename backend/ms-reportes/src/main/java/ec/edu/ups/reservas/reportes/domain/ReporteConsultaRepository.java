package ec.edu.ups.reservas.reportes.domain;

import ec.edu.ups.reservas.reportes.domain.port.out.ReporteConsultaRepositoryPort;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporteConsultaRepository extends JpaRepository<ReporteConsulta, UUID>, ReporteConsultaRepositoryPort {

    @Override
    ReporteConsulta save(ReporteConsulta consulta);
}
