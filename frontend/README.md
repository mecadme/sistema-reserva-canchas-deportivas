# Frontend

El frontend se organizará como un `shell` host y tres remotes integrados en tiempo de ejecución mediante Module Federation.

| Aplicación | Tipo | Responsabilidad |
| --- | --- | --- |
| `shell` | Host | Layout, navegación, sesión, autorización y carga de remotes |
| `mf-clientes` | Remote | Disponibilidad, nueva reserva, historial y cancelación |
| `mf-administracion` | Remote | Canchas, horarios, bloqueos, usuarios y reservas globales |
| `mf-reportes` | Remote | Indicadores básicos para administradores |

## Decisiones tomadas

- Framework: Angular 20 (standalone components), gestor de paquetes pnpm.
- Module Federation: Webpack 5 vía `@angular-architects/module-federation`.
- Sin librería visual: CSS propio.
- Sesión: mock local (servicio de auth + localStorage) mientras no haya backend integrado.

## Puertos (desarrollo local)

| Aplicación | Puerto | URL |
| --- | --- | --- |
| `shell` | 4300 | http://localhost:4300 |
| `mf-clientes` | 4201 | http://localhost:4201 (remoteEntry.js) |
| `mf-administracion` | 4202 | pendiente de scaffold |
| `mf-reportes` | 4203 | pendiente de scaffold |

## Acuerdos pendientes

- Estrategia de pruebas unitarias y de integración.

Cada aplicación debe poder instalarse, probarse y ejecutarse de forma independiente.
