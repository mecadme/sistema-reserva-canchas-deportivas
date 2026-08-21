# ms-canchas

Microservicio Spring Boot del catálogo de canchas, deportes, horarios de atención, bloqueos de mantenimiento y estado activo o inactivo.

Endpoints principales:

- `GET /api/v1/canchas`
- `POST /api/v1/canchas` (administrador)
- `PUT /api/v1/canchas/{id}` (administrador)
- `PATCH /api/v1/canchas/{id}/estado` (administrador)
- `POST /api/v1/canchas/{id}/mantenimientos` (administrador)
