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

Sirve en http://localhost:4201. Expone `./Component` (componente raíz) para ser consumido por `shell`.
