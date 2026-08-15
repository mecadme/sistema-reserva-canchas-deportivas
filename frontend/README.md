# Frontend

El frontend se organizará como un `shell` host y tres remotes integrados en tiempo de ejecución mediante Module Federation.

| Aplicación | Tipo | Responsabilidad |
| --- | --- | --- |
| `shell` | Host | Layout, navegación, sesión, autorización y carga de remotes |
| `mf-reservas` | Remote | Disponibilidad, nueva reserva, historial y cancelación |
| `mf-administracion` | Remote | Canchas, horarios, bloqueos, usuarios y reservas globales |
| `mf-reportes` | Remote | Indicadores básicos para administradores |

## Acuerdos pendientes

- Framework, lenguaje y gestor de paquetes.
- Herramienta de Module Federation: Webpack 5 o Rsbuild.
- Librería visual y criterios de accesibilidad.
- Manejo de sesión, rutas y estado compartido.
- Convención de puertos y URL de cada remote.
- Estrategia de pruebas unitarias y de integración.

Cada aplicación debe poder instalarse, probarse y ejecutarse de forma independiente.
