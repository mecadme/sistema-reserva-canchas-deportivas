# Sistema de Reserva de Canchas Deportivas

Monorepo del proyecto integrador de la asignatura **Desarrollo de Aplicaciones Empresariales**. El sistema permite reservar canchas de pádel, tenis y básquet mediante una arquitectura de microfrontends, microservicios y PostgreSQL.

## Alcance inicial

- Usuario final: registrarse, iniciar sesión, consultar disponibilidad, crear reservas, consultar sus reservas y cancelarlas antes de su inicio.
- Administrador: gestionar canchas, horarios, bloqueos, usuarios y cualquier reserva.
- Reportes: ocupación por cancha, reservas y cancelaciones por período, y demanda por cancha o deporte.
- Fuera de alcance: pagos, notificaciones, recurrencias, torneos, aplicación móvil nativa y BI avanzado.

El documento fuente está versionado en [docs/requirements/Alcance_Funcional_Reserva_Canchas_v2.pdf](docs/requirements/Alcance_Funcional_Reserva_Canchas_v2.pdf).

## Arquitectura implementada

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

Los microfrontends se integran mediante Module Federation. Los servicios exponen API REST documentadas con OpenAPI y son responsables de sus propios datos en PostgreSQL.

El sistema completo ha sido levantado y probado funcionalmente con Docker Compose. Permanecen pendientes las pruebas automatizadas end-to-end.

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

La distribución del equipo se encuentra en [docs/TEAM.md](docs/TEAM.md). Cada frente tiene dos responsables, pero los contratos OpenAPI y los flujos de extremo a extremo se revisan entre frontend y backend.

## Hoja de ruta

Las fases previstas, desde los acuerdos técnicos iniciales hasta la entrega final, están en [docs/ROADMAP.md](docs/ROADMAP.md).

## Cómo levantar el proyecto

El sistema completo (4 bases PostgreSQL + 4 microservicios + shell + 3 microfrontends) se levanta con un solo comando usando Docker Compose:

```bash
cp infra/.env.example infra/.env
docker compose -f infra/docker-compose.yml up --build
```

La aplicación queda disponible en http://localhost:4300. Detalles de puertos, URLs de Swagger, credenciales de demostración y el modo de desarrollo sin Docker están en [infra/README.md](infra/README.md).

## Flujo de trabajo

1. Crear una rama desde `integracion-completa-sistema`: `feat/<componente>-<descripcion>`.
2. Hacer commits pequeños siguiendo Conventional Commits.
3. Abrir un pull request hacia `integracion-completa-sistema` y solicitar al menos una revisión.
4. Integrar `integracion-completa-sistema` en `main` únicamente para versiones estables o entregas.

Las reglas completas están en [CONTRIBUTING.md](CONTRIBUTING.md).

## Decisiones técnicas vigentes

- Frontend con Angular 20, componentes standalone, pnpm y Module Federation sobre Webpack 5.
- Backend con Java 17, Spring Boot y Maven multi-módulo.
- Cuatro microservicios, cada uno propietario de su base PostgreSQL.
- Autenticación JWT con roles `USUARIO` y `ADMINISTRADOR`.
- Comunicación interna REST protegida mediante `X-Service-Key`.
- Sin API Gateway o BFF; los microfrontends consumen directamente las API públicas de los servicios.
- Ejecución local integrada mediante Docker Compose.

La arquitectura detallada está en [docs/architecture/README.md](docs/architecture/README.md) y las instrucciones de ejecución en [infra/README.md](infra/README.md).

## Trabajo pendiente

- Pruebas automatizadas end-to-end de los flujos principales.
- Consolidación posterior de contratos OpenAPI y evidencias de pruebas.
- Preparación de los entregables académicos finales en los formatos solicitados.

Las credenciales incluidas son exclusivamente para demostración local. No se deben subir credenciales reales ni archivos `.env` al repositorio.
