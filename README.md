# Sistema de Reserva de Canchas Deportivas

Monorepo del proyecto integrador de la asignatura **Desarrollo de Aplicaciones Empresariales**. El sistema permitirá reservar canchas de pádel, tenis y básquet mediante una arquitectura de microfrontends, microservicios y PostgreSQL.

## Alcance inicial

- Usuario final: registrarse, iniciar sesión, consultar disponibilidad, crear reservas, consultar sus reservas y cancelarlas antes de su inicio.
- Administrador: gestionar canchas, horarios, bloqueos, usuarios y cualquier reserva.
- Reportes: ocupación por cancha, reservas y cancelaciones por período, y demanda por cancha o deporte.
- Fuera de alcance: pagos, notificaciones, recurrencias, torneos, aplicación móvil nativa y BI avanzado.

El documento fuente está versionado en [docs/requirements/Alcance_Funcional_Reserva_Canchas_v2.pdf](docs/requirements/Alcance_Funcional_Reserva_Canchas_v2.pdf).

## Arquitectura prevista

| Área | Componente | Responsabilidad |
| --- | --- | --- |
| Frontend | `shell` | Layout, navegación, autenticación y orquestación de remotes |
| Frontend | `mf-clientes` | Disponibilidad, creación, historial y cancelación de reservas |
| Frontend | `mf-administracion` | Gestión de canchas, horarios, bloqueos, usuarios y reservas |
| Frontend | `mf-reportes` | Indicadores básicos de ocupación y uso |
| Backend | `ms-usuarios` | Registro, autenticación, usuarios y roles |
| Backend | `ms-canchas` | Canchas, deportes y horarios de atención |
| Backend | `ms-reservas` | Disponibilidad, creación, consulta, cancelación y reglas RN-01 a RN-08 |
| Backend | `ms-reportes` | Agregación de datos para reportes básicos |

Los microfrontends se integrarán mediante Module Federation. Los servicios expondrán API REST documentadas con OpenAPI y serán responsables de sus propios datos en PostgreSQL.

## Estructura

```text
.
├── frontend/          # Shell y microfrontends remotos
├── backend/           # Microservicios Spring Boot
├── database/          # DDL, migraciones y datos de prueba
├── infra/             # Docker Compose y configuración local
├── docs/              # Alcance, arquitectura y decisiones
└── .github/           # Plantilla de pull request
```

## Organización del equipo

La propuesta inicial para cuatro integrantes se encuentra en [docs/TEAM.md](docs/TEAM.md). Cada frente tiene dos responsables, pero los contratos OpenAPI y los flujos de extremo a extremo se revisan entre frontend y backend.

## Flujo de trabajo

1. Crear una rama desde `develop`: `feat/<componente>-<descripcion>`.
2. Hacer commits pequeños siguiendo Conventional Commits.
3. Abrir un pull request hacia `develop` y solicitar al menos una revisión.
4. Integrar `develop` en `main` únicamente para versiones estables o entregas.

Las reglas completas están en [CONTRIBUTING.md](CONTRIBUTING.md).

## Primeras decisiones pendientes

- Framework y versión del frontend, manteniendo Module Federation.
- Versiones de Java, Spring Boot y herramienta de construcción.
- Estrategia de autenticación básica con roles.
- Uso opcional de API Gateway o BFF.
- Contratos OpenAPI y modelo de datos por microservicio.
- Convención de puertos y variables de entorno para Docker Compose.

No se deben subir credenciales ni archivos `.env` al repositorio.
