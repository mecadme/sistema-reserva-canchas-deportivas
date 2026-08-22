# ms-reservas

Microservicio Spring Boot de disponibilidad, creación, consulta y cancelación de reservas. Aplica RN-01 a RN-08 y utiliza una restricción de exclusión PostgreSQL para impedir solapamientos concurrentes.

Endpoints principales:

- `GET /api/v1/disponibilidad?canchaId=&fecha=`
- `POST /api/v1/reservas`
- `GET /api/v1/reservas/mias`
- `PATCH /api/v1/reservas/{id}/cancelacion`
- `GET /api/v1/reservas` (administrador)
