# mf-administracion

Remote administrativo para gestionar canchas, horarios, bloqueos, usuarios y reservas globales.

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

Consume las API reales de los microservicios para:

- Crear, editar, activar e inactivar canchas.
- Registrar bloqueos por mantenimiento.
- Consultar y activar o inactivar usuarios.
- Consultar todas las reservas y cancelarlas como administrador.

El flujo funcional está validado manualmente; las pruebas automatizadas end-to-end permanecen pendientes.
