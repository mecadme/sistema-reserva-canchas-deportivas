# mf-clientes

Remote para consultar disponibilidad, crear reservas, mostrar el historial del usuario y cancelar reservas propias válidas.

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

Sirve en http://localhost:4201. Expone `./Routes` para ser cargado por el `shell` en la ruta `/clientes`.

Consume las API reales de `ms-canchas` y `ms-reservas` para:

- Consultar canchas y disponibilidad por fecha.
- Crear una reserva.
- Consultar las reservas propias.
- Cancelar una reserva válida.

El flujo funcional está validado manualmente; las pruebas automatizadas end-to-end permanecen pendientes.
