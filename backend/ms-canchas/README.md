# ms-canchas

Microservicio Spring Boot del catálogo de canchas, deportes, horarios de atención, bloqueos de mantenimiento y estado activo o inactivo.

- Puerto: `8082`
- Base de datos: `canchas_db`
- Swagger: http://localhost:8082/swagger-ui.html

Endpoints principales:

- `GET /api/v1/canchas`
- `POST /api/v1/canchas` (administrador)
- `PUT /api/v1/canchas/{id}` (administrador)
- `PATCH /api/v1/canchas/{id}/estado` (administrador)
- `POST /api/v1/canchas/{id}/mantenimientos` (administrador)

Los endpoints internos permiten a `ms-reservas` y `ms-reportes` consultar canchas y bloqueos mediante `X-Service-Key`. El servicio está validado funcionalmente; las pruebas automatizadas end-to-end permanecen pendientes.
