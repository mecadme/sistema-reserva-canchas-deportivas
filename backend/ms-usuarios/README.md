# ms-usuarios

Microservicio Spring Boot de registro, autenticación JWT y gestión de usuarios con roles `USUARIO` y `ADMINISTRADOR`.

Endpoints principales:

- `POST /api/v1/auth/registro`
- `POST /api/v1/auth/login`
- `GET /api/v1/usuarios/me`
- `GET /api/v1/usuarios` (administrador)
- `PATCH /api/v1/usuarios/{id}/estado` (administrador)
