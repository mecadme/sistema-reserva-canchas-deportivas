# mf-reportes

Remote de solo lectura para visualizar ocupación, reservas, cancelaciones y demanda. Su acceso está restringido al rol `ADMINISTRADOR`.

## Stack

- Angular 20 (standalone components)
- Webpack 5 Module Federation vía `@angular-architects/module-federation`
- pnpm como gestor de paquetes
- CSS propio

## Desarrollo

```bash
pnpm install
pnpm start
```

Sirve en http://localhost:4203. Expone `./Routes` para ser cargado por el `shell` en la ruta `/reportes`.

Consume la API real de `ms-reportes` para consultar indicadores en un rango de fechas. El flujo funcional está validado manualmente; las pruebas automatizadas end-to-end permanecen pendientes.
