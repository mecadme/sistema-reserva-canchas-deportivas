# Guía de contribución

## Ramas

- `main`: código estable y entregable.
- `develop`: integración continua del equipo.
- `feat/<componente>-<descripcion>`: nueva funcionalidad.
- `fix/<componente>-<descripcion>`: corrección.
- `docs/<descripcion>`: documentación.
- `chore/<descripcion>`: mantenimiento técnico.

Ejemplos: `feat/ms-reservas-validar-solapamiento` y `feat/mf-reservas-calendario`.

No se trabaja directamente sobre `main` ni `develop`. Antes de abrir un pull request, actualizar la rama con los cambios recientes de `develop`.

## Commits

Usar Conventional Commits:

```text
feat(ms-reservas): valida solapamiento de horarios
fix(shell): conserva la sesión al recargar
docs(arquitectura): registra decisión de autenticación
```

## Pull requests

Cada pull request debe:

- Tener un objetivo concreto y un alcance fácil de revisar.
- Indicar el componente afectado y la regla de negocio relacionada, si aplica.
- Incluir pruebas o explicar por qué no corresponden.
- Actualizar OpenAPI, documentación o configuración cuando cambie un contrato.
- No incluir secretos, artefactos compilados ni dependencias generadas.
- Recibir al menos una aprobación antes de integrarse.

Los cambios de contrato entre frontend y backend deben revisarse por una persona de cada frente.

## Definición de terminado

Una tarea se considera terminada cuando:

- Cumple sus criterios de aceptación.
- Las pruebas relevantes pasan localmente.
- El código puede levantarse con instrucciones reproducibles.
- La documentación y el contrato OpenAPI están actualizados.
- No rompe los flujos existentes ni introduce secretos.
- El pull request fue revisado e integrado en `develop`.

## Reglas de integración

- Cada microfrontend debe poder ejecutarse de forma independiente.
- Cada microservicio es dueño de su modelo de datos.
- Ningún servicio accede directamente a las tablas de otro servicio.
- Las integraciones entre servicios usan API REST.
- Los cambios incompatibles de API deben acordarse antes de implementarse.
