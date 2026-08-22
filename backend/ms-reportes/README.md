# ms-reportes

Microservicio Spring Boot de reportes básicos. Obtiene información mediante las API internas de canchas y reservas, sin consultar directamente sus tablas. Su base `reportes_db` conserva la auditoría de generación de reportes.

- Puerto: `8084`
- Base de datos: `reportes_db`
- Swagger: http://localhost:8084/swagger-ui.html

Endpoint principal:

- `GET /api/v1/reportes/ocupacion?desde=YYYY-MM-DD&hasta=YYYY-MM-DD` (administrador)

La comunicación con `ms-canchas` y `ms-reservas` utiliza REST interno y `X-Service-Key`. El flujo funcional está validado manualmente; las pruebas automatizadas end-to-end permanecen pendientes.
