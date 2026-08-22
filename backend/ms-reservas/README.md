# ms-reservas

Microservicio Spring Boot de disponibilidad, creación, consulta y cancelación de reservas. Aplica RN-01 a RN-08 y utiliza una restricción de exclusión PostgreSQL para impedir solapamientos concurrentes.

- Puerto: `8083`
- Base de datos: `reservas_db`
- Swagger: http://localhost:8083/swagger-ui.html

Endpoints principales:

- `GET /api/v1/disponibilidad?canchaId=&fecha=`
- `POST /api/v1/reservas`
- `GET /api/v1/reservas/mias`
- `PATCH /api/v1/reservas/{id}/cancelacion`
- `GET /api/v1/reservas` (administrador)

El servicio consulta `ms-canchas` mediante REST interno y `X-Service-Key`. El flujo funcional está validado manualmente; las pruebas automatizadas end-to-end permanecen pendientes.
