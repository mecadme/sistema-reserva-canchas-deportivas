# Infraestructura local

Levanta el sistema completo (4 microservicios + 4 bases PostgreSQL + shell + 3 microfrontends) con un solo comando, usando Docker Compose.

## Prerrequisitos

- Docker y Docker Compose.
- Puertos libres en el host: `4300`, `4201`, `4202`, `4203` (frontend) y `8081`–`8084` (backend).

## Levantar todo

```bash
cp infra/.env.example infra/.env
docker compose -f infra/docker-compose.yml up --build
```

La primera vez tarda varios minutos (build de 4 imágenes Java + 4 imágenes Angular). Las siguientes veces reutiliza la caché de Docker.

## URLs disponibles

| Servicio | URL |
| --- | --- |
| Frontend (shell) | http://localhost:4300 |
| `mf-clientes` (standalone, solo dev) | http://localhost:4201 |
| `mf-administracion` (standalone, solo dev) | http://localhost:4202 |
| `mf-reportes` (standalone, solo dev) | http://localhost:4203 |
| Swagger `ms-usuarios` | http://localhost:8081/swagger-ui.html |
| Swagger `ms-canchas` | http://localhost:8082/swagger-ui.html |
| Swagger `ms-reservas` | http://localhost:8083/swagger-ui.html |
| Swagger `ms-reportes` | http://localhost:8084/swagger-ui.html |

Para usar la aplicación entra siempre por **http://localhost:4300** — es el único punto de entrada real; los puertos 4201-4203 solo sirven para inspeccionar un microfrontend de forma aislada.

## Credenciales de demostración

| Rol | Email | Password |
| --- | --- | --- |
| Administrador | `admin@canchas.local` | `Admin123*` (o el valor de `BOOTSTRAP_ADMIN_PASSWORD` en `.env`) |

Para probar como cliente, regístrate desde la pantalla de registro del `shell` (`http://localhost:4300/registro`) — el registro siempre crea un usuario con rol `USUARIO`.

## Estado actual: frontend conectado al backend real

El frontend hace llamadas HTTP reales a los 4 microservicios (login/registro vía `ms-usuarios`, disponibilidad y reservas vía `ms-reservas`, canchas vía `ms-canchas`, reportes vía `ms-reportes`). Ya no opera en modo mock. CORS está habilitado en los 4 servicios solo para el origen `http://localhost:4300` (el `shell`).

## Ejecución sin Docker (modo desarrollo)

Para desarrollo día a día, sin reconstruir imágenes en cada cambio:

**Backend** (requiere Java 17 y PostgreSQL local o vía `backend/docker-compose.yml`):
```bash
cd backend
cp .env.example .env
docker compose up --build
```

**Frontend** (requiere Node 20+ y pnpm; instalar dependencias una sola vez, desde la raíz del workspace):
```bash
cd frontend
pnpm install
```

Luego, 4 terminales, en este orden (los remotes antes que el shell):
```bash
pnpm --filter mf-clientes start        # puerto 4201
pnpm --filter mf-administracion start  # puerto 4202
pnpm --filter mf-reportes start        # puerto 4203
pnpm --filter shell start              # puerto 4300 — abrir este al final
```

`frontend/package.json` trae atajos equivalentes: `pnpm clientes`, `pnpm administracion`, `pnpm reportes`, `pnpm shell`.

## Apagar y limpiar

```bash
docker compose -f infra/docker-compose.yml down       # detiene los contenedores
docker compose -f infra/docker-compose.yml down -v     # además borra los volúmenes de PostgreSQL (pierdes los datos)
```
