# Frontend

El frontend es un `shell` host y tres remotes integrados en tiempo de ejecución mediante Module Federation.

| Aplicación | Tipo | Responsabilidad |
| --- | --- | --- |
| `shell` | Host | Layout, navegación, sesión, autorización y carga de remotes |
| `mf-clientes` | Remote | Disponibilidad, nueva reserva, historial y cancelación |
| `mf-administracion` | Remote | Canchas, horarios, bloqueos, usuarios y reservas globales |
| `mf-reportes` | Remote | Indicadores básicos para administradores |

## Decisiones tomadas

- Framework: Angular 20 (standalone components), gestor de paquetes **pnpm** (workspace único en esta carpeta, ver `pnpm-workspace.yaml`).
- Module Federation: Webpack 5 vía `@angular-architects/module-federation`.
- Sin librería visual: CSS propio.
- Sesión: autenticación JWT real mediante `ms-usuarios`, conservada en `localStorage` por el `shell`.
- Integración HTTP real con `ms-usuarios`, `ms-canchas`, `ms-reservas` y `ms-reportes`.
- Sin API Gateway/BFF; cada microfrontend consume las API públicas que necesita.

## Instalación

Un solo `pnpm install` en esta carpeta resuelve las dependencias de las 4 apps a la vez, garantizando que compartan exactamente las mismas versiones de `@angular/*` (necesario porque Module Federation usa `shareAll({ singleton: true, strictVersion: true })`).

```bash
pnpm install
```

## Levantar en desarrollo

Los remotes primero, el `shell` al final (carga los `remoteEntry.js` de los otros 3 en runtime):

```bash
pnpm --filter mf-clientes start        # puerto 4201
pnpm --filter mf-administracion start  # puerto 4202
pnpm --filter mf-reportes start        # puerto 4203
pnpm --filter shell start              # puerto 4300 — abrir este al final
```

Atajos equivalentes definidos en `package.json`: `pnpm clientes`, `pnpm administracion`, `pnpm reportes`, `pnpm shell`.

## Puertos

| Aplicación | Puerto | URL |
| --- | --- | --- |
| `shell` | 4300 | http://localhost:4300 |
| `mf-clientes` | 4201 | http://localhost:4201 (remoteEntry.js) |
| `mf-administracion` | 4202 | http://localhost:4202 (remoteEntry.js) |
| `mf-reportes` | 4203 | http://localhost:4203 (remoteEntry.js) |

## Estado actual

- Los cuatro proyectos construyen y se despliegan de forma independiente.
- El `shell` carga los tres remotes en tiempo de ejecución.
- Los flujos de autenticación, reservas, administración y reportes consumen el backend real.
- El sistema completo ha sido levantado y validado funcionalmente.
- Las pruebas automatizadas end-to-end permanecen pendientes.

Cada aplicación puede construirse y desplegarse de forma independiente (ver `Dockerfile` en cada una), aunque comparten un único `pnpm-lock.yaml` a nivel de workspace.
