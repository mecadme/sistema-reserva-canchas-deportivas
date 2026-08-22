package ec.edu.ups.reservas.canchas.domain;

import ec.edu.ups.reservas.canchas.domain.port.out.BloqueoRepositoryPort;
import java.time.OffsetDateTime;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface BloqueoRepository extends JpaRepository<BloqueoMantenimiento, UUID>, BloqueoRepositoryPort {

    List<BloqueoMantenimiento> findByCanchaIdOrderByInicio(UUID canchaId);

    @Query("select count(b) > 0 from BloqueoMantenimiento b where b.canchaId = :canchaId " +
           "and b.inicio < :fin and b.fin > :inicio")
    boolean existeSolapamiento(@Param("canchaId") UUID canchaId,
                               @Param("inicio") OffsetDateTime inicio,
                               @Param("fin") OffsetDateTime fin);

    @Query("select b from BloqueoMantenimiento b where b.canchaId = :canchaId " +
           "and b.inicio < :fin and b.fin > :inicio order by b.inicio")
    List<BloqueoMantenimiento> findSolapamientos(@Param("canchaId") UUID canchaId,
                                                 @Param("inicio") OffsetDateTime inicio,
                                                 @Param("fin") OffsetDateTime fin);

    @Override
    BloqueoMantenimiento save(BloqueoMantenimiento bloqueo);
}
