# mf-administracion

Remote administrativo para gestionar canchas, horarios, bloqueos, usuarios y reservas globales.

> Estado: scaffold base con Module Federation ya integrado al shell. Todavía no tiene pantallas funcionales — solo una página placeholder para validar el wiring end-to-end.

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

Sirve en http://localhost:4202. Expone `./Routes` para ser consumido por `shell` en la ruta `/admin`.
