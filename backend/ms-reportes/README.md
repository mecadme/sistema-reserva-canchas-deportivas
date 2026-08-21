# ms-reportes

Microservicio Spring Boot de reportes básicos. Obtiene información mediante las API internas de canchas y reservas, sin consultar directamente sus tablas. Su base `reportes_db` conserva la auditoría de generación de reportes.

Endpoint principal:

- `GET /api/v1/reportes/ocupacion?desde=YYYY-MM-DD&hasta=YYYY-MM-DD` (administrador)
