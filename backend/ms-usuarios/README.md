# ms-usuarios

Microservicio Spring Boot de registro, autenticación JWT y gestión de usuarios con roles `USUARIO` y `ADMINISTRADOR`.

- Puerto: `8081`
- Base de datos: `usuarios_db`
- Swagger: http://localhost:8081/swagger-ui.html

Endpoints principales:

- `POST /api/v1/auth/registro`
- `POST /api/v1/auth/login`
- `GET /api/v1/usuarios/me`
- `GET /api/v1/usuarios` (administrador)
- `PATCH /api/v1/usuarios/{id}/estado` (administrador)

El servicio está integrado con el frontend real y validado funcionalmente. Las pruebas automatizadas end-to-end permanecen pendientes.
