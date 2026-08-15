# Organización inicial del equipo

La distribución busca mantener dos personas en frontend y dos en backend, con responsabilidades primarias claras y revisión cruzada.

| Rol | Responsabilidad primaria | Responsabilidad secundaria | Integrante |
| --- | --- | --- | --- |
| Frontend 1 | `shell` y autenticación en la interfaz | `mf-reservas` | Por asignar |
| Frontend 2 | `mf-administracion` | `mf-reportes` | Por asignar |
| Backend 1 | `ms-usuarios` | `ms-canchas` | Por asignar |
| Backend 2 | `ms-reservas` | `ms-reportes` | Por asignar |

## Acuerdos de colaboración

- La responsabilidad primaria no implica propiedad exclusiva del código.
- Cada cambio relevante debe tener revisión de otro integrante.
- Un cambio de contrato se acuerda entre la persona responsable del microfrontend y la del microservicio afectados.
- Las historias se dividen en entregas verticales para evitar que frontend y backend avancen sin integración.
- Las ausencias o bloqueos se registran en la tarea correspondiente para que otro integrante pueda continuar.

## Parejas de integración sugeridas

- Reservas: Frontend 1 + Backend 2.
- Administración de canchas y usuarios: Frontend 2 + Backend 1.
- Reportes: Frontend 2 + Backend 2.
- Autenticación y autorización: Frontend 1 + Backend 1.
