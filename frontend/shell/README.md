# shell

Host de Module Federation responsable del layout, navegación, autenticación JWT, autorización por roles y carga de los microfrontends remotos.

## Stack

- Angular 20 (standalone components)
- Webpack 5 Module Federation vía `@angular-architects/module-federation`
- pnpm como gestor de paquetes
- Sin librería de UI (CSS propio)

## Desarrollo

```bash
pnpm install
pnpm start
```

Sirve en http://localhost:4300 y carga:

- `mf-clientes` desde http://localhost:4201.
- `mf-administracion` desde http://localhost:4202.
- `mf-reportes` desde http://localhost:4203.

El `shell` gestiona el inicio de sesión y registro mediante `ms-usuarios`, conserva el JWT y protege las rutas según los roles `USUARIO` y `ADMINISTRADOR`.

El flujo integrado está validado manualmente; las pruebas automatizadas end-to-end permanecen pendientes.
