workspace "Sistema de Reserva de Canchas Deportivas" "Modelo C4 (Contexto, Contenedores, Componentes) del sistema de reserva de canchas de pádel, tenis y básquet." {

    !identifiers hierarchical

    model {
        properties {
            "structurizr.groupSeparator" "/"
        }

        usuarioFinal = person "Usuario Final" "Persona que consulta disponibilidad, crea, consulta y cancela sus propias reservas de canchas."
        administrador = person "Administrador" "Persona que gestiona el catálogo de canchas, horarios, usuarios y visualiza reportes de ocupación del sistema."

        sistemaReservas = softwareSystem "Sistema de Reserva de Canchas" "Permite consultar disponibilidad, reservar y cancelar canchas de pádel, tenis y básquet; y administrar el catálogo, usuarios y reportes de uso." {

            group "Frontend" {
                shell = container "Shell" "Aplicación contenedora: layout, navegación, autenticación (JWT) y orquestación de los microfrontends remotos vía Module Federation." "Angular 20, Webpack 5 (Module Federation)" "Frontend, MF" {
                    appRoutes = component "AppRoutes" "Configuración de rutas del shell; carga en runtime las rutas expuestas por cada microfrontend remoto (loadChildren vía Module Federation)." "Angular Router" "Logic"
                    guestGuard = component "GuestGuard" "Guard que redirige a un usuario ya autenticado fuera de las páginas públicas (landing, login, registro)." "Angular CanActivateFn" "Pipeline"
                    roleGuard = component "RoleGuard" "Guard que restringe el acceso a una ruta según el rol del usuario autenticado (cliente / admin)." "Angular CanActivateFn" "Pipeline"
                    authInterceptor = component "AuthInterceptor" "Interceptor HTTP que agrega el header Authorization: Bearer <token> a las peticiones dirigidas a los microservicios." "Angular HttpInterceptorFn" "Pipeline"
                    authService = component "AuthService" "Login, registro y gestión de la sesión (JWT); persiste la sesión en localStorage y expone el usuario actual." "Angular Service" "Service"
                    authenticatedLayout = component "AuthenticatedLayout" "Layout de las rutas autenticadas: hospeda el router-outlet donde se montan los microfrontends remotos y el menú de administración." "Angular Component" "UI"
                    adminNav = component "AdminNav" "Barra de navegación visible únicamente para el rol administrador." "Angular Component" "UI"
                    landing = component "Landing" "Página de bienvenida pública, previa al login." "Angular Component" "Web"
                    login = component "Login" "Formulario de inicio de sesión." "Angular Component" "Web"
                    register = component "Register" "Formulario de registro de usuario final." "Angular Component" "Web"
                }
                mfClientes = container "mf-clientes" "Consulta de disponibilidad, creación, cancelación e historial de reservas para el usuario final." "Angular 20, Webpack 5 (Module Federation - remote)" "Frontend, MF" {
                    mfcRoutes = component "Routes" "Rutas expuestas por el remote (expose './Routes'); mapea '' a Home y 'reservas' a Reservas." "Angular Router" "Logic"
                    mfcAuthInterceptor = component "AuthInterceptor" "Interceptor HTTP que agrega el header Authorization: Bearer <token> a las peticiones dirigidas a los microservicios." "Angular HttpInterceptorFn" "Pipeline"
                    home = component "Home" "Pantalla de consulta de disponibilidad y creación de una nueva reserva: elige cancha, fecha y bloque horario." "Angular Component" "Web"
                    reservas = component "Reservas" "Pantalla 'Mis reservas': lista las reservas del usuario y permite cancelarlas." "Angular Component" "Web"
                    canchasService = component "CanchasService" "Obtiene el listado de canchas disponibles para elegir." "Angular Service" "Service"
                    availabilityService = component "AvailabilityService" "Consulta los bloques horarios disponibles/ocupados de una cancha en una fecha." "Angular Service" "Service"
                    reservationsService = component "ReservationsService" "Crea, lista y cancela las reservas del usuario autenticado." "Angular Service" "Service"
                }
                mfAdministracion = container "mf-administracion" "Gestión de canchas, horarios, bloqueos de mantenimiento, usuarios y reservas globales para el administrador." "Angular 20, Webpack 5 (Module Federation - remote)" "Frontend, MF" {
                    mfaRoutes = component "Routes" "Rutas expuestas por el remote (expose './Routes'): '', 'canchas', 'usuarios' y 'reservas'." "Angular Router" "Logic"
                    mfaAuthInterceptor = component "AuthInterceptor" "Interceptor HTTP que agrega el header Authorization: Bearer <token> a las peticiones dirigidas a los microservicios." "Angular HttpInterceptorFn" "Pipeline"
                    homeAdmin = component "Panel (Home)" "Panel resumen: canchas activas, usuarios activos, reservas confirmadas y canceladas." "Angular Component" "Web"
                    canchasPage = component "Canchas" "ABM de canchas (nombre, deporte, horario, activar/inactivar) y gestión de bloqueos de mantenimiento." "Angular Component" "Web"
                    usuariosPage = component "Usuarios" "Listado de usuarios con opción de activar/inactivar." "Angular Component" "Web"
                    reservasAdminPage = component "Reservas" "Listado global de reservas con opción de cancelar cualquiera." "Angular Component" "Web"
                    canchasService = component "CanchasService" "ABM de canchas y de bloqueos de mantenimiento." "Angular Service" "Service"
                    usuariosService = component "UsuariosService" "Lista usuarios y cambia su estado (activo/inactivo)." "Angular Service" "Service"
                    reservasAdminService = component "ReservasAdminService" "Lista todas las reservas (enriquecidas con el nombre de usuario) y cancela cualquier reserva." "Angular Service" "Service"
                }
                mfReportes = container "mf-reportes" "Visualización de reportes básicos de ocupación, reservas por período/deporte y cancelaciones." "Angular 20, Webpack 5 (Module Federation - remote)" "Frontend, MF" {
                    mfrRoutes = component "Routes" "Rutas expuestas por el remote (expose './Routes'); mapea '' a la pantalla Reportes." "Angular Router" "Logic"
                    mfrAuthInterceptor = component "AuthInterceptor" "Interceptor HTTP que agrega el header Authorization: Bearer <token> a las peticiones dirigidas a los microservicios." "Angular HttpInterceptorFn" "Pipeline"
                    reportes = component "Reportes" "Pantalla de reportes básicos: ocupación por cancha, reservas por deporte, totales por estado y canchas de mayor/menor demanda, filtrando por rango de fechas." "Angular Component" "Web"
                    reportesService = component "ReportesService" "Obtiene el reporte de ocupación para un rango de fechas." "Angular Service" "Service"
                }
            }

            group "Bounded Context: Seguridad" {
                msUsuarios = container "ms-usuarios" "Registro, autenticación (login JWT) y gestión de usuarios y roles (administrador / usuario final)." "Java 17, Spring Boot" "Backend" {
                    authController = component "AuthController" "Expone POST /api/v1/auth/registro y POST /api/v1/auth/login." "Spring REST Controller" "API"
                    usuarioController = component "UsuarioController" "Expone GET /api/v1/usuarios/me, GET /api/v1/usuarios y PATCH /api/v1/usuarios/{id}/estado." "Spring REST Controller" "API"
                    internalUsuarioController = component "InternalUsuarioController" "Expone GET /internal/v1/usuarios/{id} para consumo interno de otros microservicios (protegido por X-Service-Key)." "Spring REST Controller" "API"
                    serviceKeyInterceptor = component "ServiceKeyInterceptor" "Valida el header X-Service-Key en las peticiones a /internal/**, autenticando el tráfico entre microservicios." "Spring HandlerInterceptor" "Pipeline"
                    usuarioService = component "UsuarioService" "Implementa AuthUseCase y UsuarioUseCase: registra, autentica, consulta y cambia el estado de usuarios." "Spring Service" "Service"
                    tokenService = component "TokenService" "Emite el JWT (HS256) tras un login exitoso: subject = id de usuario, claim roles = rol." "Spring Service" "Service"
                    usuarioRepository = component "UsuarioRepository" "Repositorio Spring Data JPA sobre la tabla usuarios." "Spring Data JPA" "Repository"
                }
                usuariosDb = container "usuarios_db" "Almacena usuarios, roles y credenciales." "PostgreSQL 17" "Database"
            }

            group "Bounded Context: Catálogo de Canchas" {
                msCanchas = container "ms-canchas" "ABM del catálogo de canchas, deportes, horarios de atención y bloqueos de mantenimiento." "Java 17, Spring Boot" "Backend" {
                    canchaController = component "CanchaController" "Expone GET/POST/PUT /api/v1/canchas, PATCH /api/v1/canchas/{id}/estado y GET/POST /api/v1/canchas/{id}/mantenimientos." "Spring REST Controller" "API"
                    internalCanchaController = component "InternalCanchaController" "Expone GET /internal/v1/canchas/{id} (detalle + bloqueada), GET /internal/v1/canchas y GET /internal/v1/canchas/{id}/bloqueos, para consumo interno de otros microservicios (protegido por X-Service-Key)." "Spring REST Controller" "API"
                    canchasServiceKeyInterceptor = component "ServiceKeyInterceptor" "Valida el header X-Service-Key en las peticiones a /internal/**, autenticando el tráfico entre microservicios." "Spring HandlerInterceptor" "Pipeline"
                    canchaService = component "CanchaService" "Implementa CanchaUseCase: ABM de canchas, bloqueos de mantenimiento y validación de solapamientos." "Spring Service" "Service"
                    canchaRepository = component "CanchaRepository" "Repositorio Spring Data JPA sobre la tabla canchas." "Spring Data JPA" "Repository"
                    bloqueoRepository = component "BloqueoRepository" "Repositorio Spring Data JPA sobre la tabla bloqueos_mantenimiento." "Spring Data JPA" "Repository"
                }
                canchasDb = container "canchas_db" "Almacena canchas, deportes, horarios de atención y bloqueos de mantenimiento." "PostgreSQL 17" "Database"
            }

            group "Bounded Context: Reservas" {
                msReservas = container "ms-reservas" "Creación, consulta y cancelación de reservas; validación de disponibilidad y reglas de negocio (RN-01 a RN-08)." "Java 17, Spring Boot" "Backend" {
                    reservaController = component "ReservaController" "Expone GET /api/v1/disponibilidad, POST /api/v1/reservas, GET /api/v1/reservas/mias, GET /api/v1/reservas y PATCH /api/v1/reservas/{id}/cancelacion." "Spring REST Controller" "API"
                    internalReservaController = component "InternalReservaController" "Expone GET /internal/v1/reservas/resumen, para consumo interno de otros microservicios (protegido por X-Service-Key)." "Spring REST Controller" "API"
                    reservasServiceKeyInterceptor = component "ServiceKeyInterceptor" "Valida el header X-Service-Key en las peticiones a /internal/**, autenticando el tráfico entre microservicios." "Spring HandlerInterceptor" "Pipeline"
                    reservaService = component "ReservaService" "Implementa ReservaUseCase: aplica RN-01 a RN-08 (bloque válido, no solapamiento, límite de reservas activas, ventana de cancelación, cálculo de disponibilidad y resumen)." "Spring Service" "Service"
                    canchaClient = component "CanchaClient" "Adaptador saliente (RestClient) que consulta a ms-canchas para validar cancha activa, horario y bloqueos de mantenimiento." "Spring RestClient" "Pipeline"
                    finalizadorJob = component "FinalizadorJob" "Job programado (cada 60s) que transiciona reservas CONFIRMADA vencidas a FINALIZADA (RN-08)." "Spring @Scheduled" "Job"
                    reservaRepository = component "ReservaRepository" "Repositorio Spring Data JPA sobre la tabla reservas; el constraint EXCLUDE de PostgreSQL aplica RN-02 a nivel de base de datos." "Spring Data JPA" "Repository"
                }
                reservasDb = container "reservas_db" "Almacena reservas y su estado (Confirmada, Cancelada, Finalizada)." "PostgreSQL 17" "Database"
            }

            group "Bounded Context: Reportes" {
                msReportes = container "ms-reportes" "Generación de reportes básicos de ocupación, reservas por período y cancelaciones." "Java 17, Spring Boot" "Backend" {
                    reporteController = component "ReporteController" "Expone GET /api/v1/reportes/ocupacion para un rango de fechas." "Spring REST Controller" "API"
                    reporteService = component "ReporteService" "Implementa ReporteUseCase: valida el rango, calcula ocupación por cancha y registra la auditoría de la consulta." "Spring Service" "Service"
                    datosClient = component "DatosClient" "Adaptador saliente (RestClient) que consulta a ms-reservas (resumen) y ms-canchas (catálogo) para construir el reporte." "Spring RestClient" "Pipeline"
                    reporteConsultaRepository = component "ReporteConsultaRepository" "Repositorio Spring Data JPA sobre la tabla reporte_consultas (auditoría: quién generó qué reporte y cuándo)." "Spring Data JPA" "Repository"
                }
                reportesDb = container "reportes_db" "Almacena auditoría de consultas de reportes generadas." "PostgreSQL 17" "Database"
            }
        }

        usuarioFinal -> sistemaReservas "Consulta disponibilidad, crea y cancela sus reservas, y revisa su historial" "HTTPS"
        administrador -> sistemaReservas "Gestiona canchas, horarios, usuarios, cancela reservas y visualiza reportes" "HTTPS"

        usuarioFinal -> sistemaReservas.shell "Usa el sistema" "HTTPS"
        administrador -> sistemaReservas.shell "Usa el sistema" "HTTPS"

        sistemaReservas.shell -> sistemaReservas.mfClientes "Carga en runtime (remoteEntry.js)" "Module Federation"
        sistemaReservas.shell -> sistemaReservas.mfAdministracion "Carga en runtime (remoteEntry.js)" "Module Federation"
        sistemaReservas.shell -> sistemaReservas.mfReportes "Carga en runtime (remoteEntry.js)" "Module Federation"
        sistemaReservas.shell -> sistemaReservas.msUsuarios "Login / registro" "JSON/HTTPS"

        sistemaReservas.shell.appRoutes -> sistemaReservas.shell.guestGuard "Protege rutas públicas (canActivate)"
        sistemaReservas.shell.appRoutes -> sistemaReservas.shell.roleGuard "Protege rutas de /clientes, /admin y /reportes (canActivate)"
        sistemaReservas.shell.appRoutes -> sistemaReservas.shell.landing "Enruta a /"
        sistemaReservas.shell.appRoutes -> sistemaReservas.shell.login "Enruta a /login"
        sistemaReservas.shell.appRoutes -> sistemaReservas.shell.register "Enruta a /registro"
        sistemaReservas.shell.appRoutes -> sistemaReservas.shell.authenticatedLayout "Enruta a las rutas autenticadas (padre)"
        sistemaReservas.shell.appRoutes -> sistemaReservas.mfClientes "loadChildren('mfClientes/Routes') en /clientes" "Module Federation"
        sistemaReservas.shell.appRoutes -> sistemaReservas.mfAdministracion "loadChildren('mfAdministracion/Routes') en /admin" "Module Federation"
        sistemaReservas.shell.appRoutes -> sistemaReservas.mfReportes "loadChildren('mfReportes/Routes') en /reportes" "Module Federation"

        sistemaReservas.shell.guestGuard -> sistemaReservas.shell.authService "Lee el usuario actual"
        sistemaReservas.shell.roleGuard -> sistemaReservas.shell.authService "Lee el usuario actual y su rol"
        sistemaReservas.shell.authenticatedLayout -> sistemaReservas.shell.authService "Lee la sesión y hace logout"
        sistemaReservas.shell.authenticatedLayout -> sistemaReservas.shell.adminNav "Renderiza (solo si role=admin)"
        sistemaReservas.shell.login -> sistemaReservas.shell.authService "Invoca login()"
        sistemaReservas.shell.register -> sistemaReservas.shell.authService "Invoca register()"
        sistemaReservas.shell.authService -> sistemaReservas.msUsuarios "POST /api/v1/auth/login, /api/v1/auth/registro, GET /api/v1/usuarios/me" "JSON/HTTPS"
        sistemaReservas.shell.authInterceptor -> sistemaReservas.msUsuarios "Agrega Authorization: Bearer <token>" "JSON/HTTPS"

        sistemaReservas.shell.authService -> sistemaReservas.msUsuarios.authController "POST /api/v1/auth/login, /api/v1/auth/registro" "JSON/HTTPS"
        sistemaReservas.shell.authService -> sistemaReservas.msUsuarios.usuarioController "GET /api/v1/usuarios/me" "JSON/HTTPS"
        sistemaReservas.mfAdministracion.usuariosService -> sistemaReservas.msUsuarios.usuarioController "GET /api/v1/usuarios, PATCH /api/v1/usuarios/{id}/estado" "JSON/HTTPS"

        sistemaReservas.msUsuarios.authController -> sistemaReservas.msUsuarios.usuarioService "registrar() / autenticar()"
        sistemaReservas.msUsuarios.authController -> sistemaReservas.msUsuarios.tokenService "emitir() el JWT tras un login exitoso"
        sistemaReservas.msUsuarios.usuarioController -> sistemaReservas.msUsuarios.usuarioService "obtener() / listar() / cambiarEstado()"
        sistemaReservas.msUsuarios.internalUsuarioController -> sistemaReservas.msUsuarios.usuarioService "obtener()"
        sistemaReservas.msUsuarios.serviceKeyInterceptor -> sistemaReservas.msUsuarios.internalUsuarioController "Protege el acceso a /internal/**"
        sistemaReservas.msUsuarios.usuarioService -> sistemaReservas.msUsuarios.usuarioRepository "CRUD de usuarios"
        sistemaReservas.msUsuarios.usuarioRepository -> sistemaReservas.usuariosDb "Lee y escribe" "JDBC"

        sistemaReservas.mfClientes -> sistemaReservas.msReservas "Consulta disponibilidad, crea y cancela reservas" "JSON/HTTPS"
        sistemaReservas.mfClientes -> sistemaReservas.msCanchas "Consulta catálogo de canchas" "JSON/HTTPS"

        sistemaReservas.mfClientes.mfcRoutes -> sistemaReservas.mfClientes.home "Enruta a '' (raíz del remote)"
        sistemaReservas.mfClientes.mfcRoutes -> sistemaReservas.mfClientes.reservas "Enruta a 'reservas'"
        sistemaReservas.mfClientes.home -> sistemaReservas.mfClientes.canchasService "Lista canchas para elegir"
        sistemaReservas.mfClientes.home -> sistemaReservas.mfClientes.availabilityService "Consulta bloques horarios disponibles"
        sistemaReservas.mfClientes.home -> sistemaReservas.mfClientes.reservationsService "Crea la reserva y cuenta las confirmadas"
        sistemaReservas.mfClientes.reservas -> sistemaReservas.mfClientes.reservationsService "Lista y cancela las reservas propias"
        sistemaReservas.mfClientes.canchasService -> sistemaReservas.msCanchas.canchaController "GET /api/v1/canchas" "JSON/HTTPS"
        sistemaReservas.mfClientes.availabilityService -> sistemaReservas.msReservas.reservaController "GET /api/v1/disponibilidad" "JSON/HTTPS"
        sistemaReservas.mfClientes.reservationsService -> sistemaReservas.msReservas.reservaController "GET/POST /api/v1/reservas, PATCH /api/v1/reservas/{id}/cancelacion" "JSON/HTTPS"
        sistemaReservas.mfClientes.mfcAuthInterceptor -> sistemaReservas.msReservas "Agrega Authorization: Bearer <token>" "JSON/HTTPS"
        sistemaReservas.mfClientes.mfcAuthInterceptor -> sistemaReservas.msCanchas "Agrega Authorization: Bearer <token>" "JSON/HTTPS"
        sistemaReservas.mfAdministracion -> sistemaReservas.msCanchas "Gestiona canchas, horarios y bloqueos" "JSON/HTTPS"
        sistemaReservas.mfAdministracion -> sistemaReservas.msUsuarios "Gestiona usuarios (activar/inactivar)" "JSON/HTTPS"
        sistemaReservas.mfAdministracion -> sistemaReservas.msReservas "Consulta y cancela reservas globales" "JSON/HTTPS"

        sistemaReservas.mfAdministracion.mfaRoutes -> sistemaReservas.mfAdministracion.homeAdmin "Enruta a '' (raíz del remote)"
        sistemaReservas.mfAdministracion.mfaRoutes -> sistemaReservas.mfAdministracion.canchasPage "Enruta a 'canchas'"
        sistemaReservas.mfAdministracion.mfaRoutes -> sistemaReservas.mfAdministracion.usuariosPage "Enruta a 'usuarios'"
        sistemaReservas.mfAdministracion.mfaRoutes -> sistemaReservas.mfAdministracion.reservasAdminPage "Enruta a 'reservas'"

        sistemaReservas.mfAdministracion.homeAdmin -> sistemaReservas.mfAdministracion.canchasService "Cuenta canchas activas"
        sistemaReservas.mfAdministracion.homeAdmin -> sistemaReservas.mfAdministracion.usuariosService "Cuenta usuarios activos"
        sistemaReservas.mfAdministracion.homeAdmin -> sistemaReservas.mfAdministracion.reservasAdminService "Cuenta reservas confirmadas/canceladas"
        sistemaReservas.mfAdministracion.canchasPage -> sistemaReservas.mfAdministracion.canchasService "ABM de canchas y bloqueos de mantenimiento"
        sistemaReservas.mfAdministracion.usuariosPage -> sistemaReservas.mfAdministracion.usuariosService "Lista y cambia el estado de usuarios"
        sistemaReservas.mfAdministracion.reservasAdminPage -> sistemaReservas.mfAdministracion.reservasAdminService "Lista y cancela reservas"
        sistemaReservas.mfAdministracion.reservasAdminService -> sistemaReservas.mfAdministracion.usuariosService "Resuelve el nombre del usuario de cada reserva"

        sistemaReservas.mfAdministracion.canchasService -> sistemaReservas.msCanchas.canchaController "GET/POST/PUT /api/v1/canchas, PATCH /estado, GET/POST /mantenimientos" "JSON/HTTPS"
        sistemaReservas.mfAdministracion.reservasAdminService -> sistemaReservas.msReservas.reservaController "GET /api/v1/reservas, PATCH /api/v1/reservas/{id}/cancelacion" "JSON/HTTPS"
        sistemaReservas.mfAdministracion.mfaAuthInterceptor -> sistemaReservas.msCanchas "Agrega Authorization: Bearer <token>" "JSON/HTTPS"
        sistemaReservas.mfAdministracion.mfaAuthInterceptor -> sistemaReservas.msUsuarios "Agrega Authorization: Bearer <token>" "JSON/HTTPS"
        sistemaReservas.mfAdministracion.mfaAuthInterceptor -> sistemaReservas.msReservas "Agrega Authorization: Bearer <token>" "JSON/HTTPS"
        sistemaReservas.mfReportes -> sistemaReservas.msReportes "Consulta reportes de ocupación y uso" "JSON/HTTPS"

        sistemaReservas.mfReportes.mfrRoutes -> sistemaReservas.mfReportes.reportes "Enruta a '' (raíz del remote)"
        sistemaReservas.mfReportes.reportes -> sistemaReservas.mfReportes.reportesService "Genera el reporte para el rango de fechas seleccionado"
        sistemaReservas.mfReportes.reportesService -> sistemaReservas.msReportes.reporteController "GET /api/v1/reportes/ocupacion" "JSON/HTTPS"
        sistemaReservas.mfReportes.mfrAuthInterceptor -> sistemaReservas.msReportes "Agrega Authorization: Bearer <token>" "JSON/HTTPS"

        sistemaReservas.msReportes.reporteController -> sistemaReservas.msReportes.reporteService "generar()"
        sistemaReservas.msReportes.reporteService -> sistemaReservas.msReportes.datosClient "resumen()/canchas()"
        sistemaReservas.msReportes.reporteService -> sistemaReservas.msReportes.reporteConsultaRepository "Registra la auditoría de la consulta"
        sistemaReservas.msReportes.reporteConsultaRepository -> sistemaReservas.reportesDb "Lee y escribe" "JDBC"
        sistemaReservas.msReportes.datosClient -> sistemaReservas.msReservas.internalReservaController "GET /internal/v1/reservas/resumen" "JSON/HTTPS, X-Service-Key"
        sistemaReservas.msReportes.datosClient -> sistemaReservas.msCanchas.internalCanchaController "GET /internal/v1/canchas" "JSON/HTTPS, X-Service-Key"

        sistemaReservas.msReservas.reservaController -> sistemaReservas.msReservas.reservaService "disponibilidad()/crear()/mias()/todas()/cancelar()"
        sistemaReservas.msReservas.internalReservaController -> sistemaReservas.msReservas.reservaService "resumen()"
        sistemaReservas.msReservas.reservasServiceKeyInterceptor -> sistemaReservas.msReservas.internalReservaController "Protege el acceso a /internal/**"
        sistemaReservas.msReservas.reservaService -> sistemaReservas.msReservas.reservaRepository "CRUD de reservas; RN-01, RN-04, RN-05, RN-06, RN-08"
        sistemaReservas.msReservas.reservaService -> sistemaReservas.msReservas.canchaClient "Valida cancha activa, horario y bloqueos (RN-02, RN-07)"
        sistemaReservas.msReservas.finalizadorJob -> sistemaReservas.msReservas.reservaService "finalizarVencidas() cada 60s (RN-08)"
        sistemaReservas.msReservas.reservaRepository -> sistemaReservas.reservasDb "Lee y escribe" "JDBC"
        sistemaReservas.msReservas.canchaClient -> sistemaReservas.msCanchas.internalCanchaController "GET /internal/v1/canchas/{id}, /internal/v1/canchas/{id}/bloqueos" "JSON/HTTPS, X-Service-Key"

        sistemaReservas.msCanchas.canchaController -> sistemaReservas.msCanchas.canchaService "listar()/crear()/actualizar()/cambiarEstado()/bloquear()/mantenimientos()"
        sistemaReservas.msCanchas.internalCanchaController -> sistemaReservas.msCanchas.canchaService "detalleInterno()/listar()/bloqueosDia()"
        sistemaReservas.msCanchas.canchasServiceKeyInterceptor -> sistemaReservas.msCanchas.internalCanchaController "Protege el acceso a /internal/**"
        sistemaReservas.msCanchas.canchaService -> sistemaReservas.msCanchas.canchaRepository "CRUD de canchas"
        sistemaReservas.msCanchas.canchaService -> sistemaReservas.msCanchas.bloqueoRepository "CRUD de bloqueos de mantenimiento y validación de solapamientos"
        sistemaReservas.msCanchas.canchaRepository -> sistemaReservas.canchasDb "Lee y escribe" "JDBC"
        sistemaReservas.msCanchas.bloqueoRepository -> sistemaReservas.canchasDb "Lee y escribe" "JDBC"
    }

    views {
        systemContext sistemaReservas "Nivel1-Contexto" {
            include usuarioFinal administrador sistemaReservas
            autoLayout lr
            description "Diagrama de contexto (C4 Nivel 1): actores y el sistema de reserva de canchas."
        }

        container sistemaReservas "Nivel2-Contenedores" {
            include *
            autoLayout tb
            description "Diagrama de contenedores (C4 Nivel 2): shell + 3 microfrontends, 4 microservicios Spring Boot y sus 4 bases PostgreSQL."
        }

        component sistemaReservas.shell "Nivel3-Componentes-Shell" {
            include *
            autoLayout tb
            description "Diagrama de componentes (C4 Nivel 3) del contenedor Shell: rutas, guards, autenticación y páginas públicas."
        }

        component sistemaReservas.mfClientes "Nivel3-Componentes-MfClientes" {
            include *
            autoLayout tb
            description "Diagrama de componentes (C4 Nivel 3) del contenedor mf-clientes: pantallas de disponibilidad/reserva, 'mis reservas' y sus servicios de datos."
        }

        component sistemaReservas.mfReportes "Nivel3-Componentes-MfReportes" {
            include *
            autoLayout tb
            description "Diagrama de componentes (C4 Nivel 3) del contenedor mf-reportes: pantalla de reportes básicos de ocupación y su servicio de datos."
        }

        component sistemaReservas.mfAdministracion "Nivel3-Componentes-MfAdministracion" {
            include *
            autoLayout tb
            description "Diagrama de componentes (C4 Nivel 3) del contenedor mf-administracion: panel resumen, gestión de canchas/bloqueos, usuarios y reservas globales."
        }

        component sistemaReservas.msUsuarios "Nivel3-Componentes-MsUsuarios" {
            include *
            autoLayout tb
            description "Diagrama de componentes (C4 Nivel 3) del contenedor ms-usuarios: autenticación JWT, gestión de usuarios y validación de tráfico interno."
        }

        component sistemaReservas.msCanchas "Nivel3-Componentes-MsCanchas" {
            include *
            autoLayout tb
            description "Diagrama de componentes (C4 Nivel 3) del contenedor ms-canchas: ABM de canchas, bloqueos de mantenimiento y API interna consumida por ms-reservas y ms-reportes."
        }

        component sistemaReservas.msReservas "Nivel3-Componentes-MsReservas" {
            include *
            autoLayout tb
            description "Diagrama de componentes (C4 Nivel 3) del contenedor ms-reservas: disponibilidad, creación/cancelación de reservas, reglas de negocio RN-01 a RN-08 y el job de finalización."
        }

        component sistemaReservas.msReportes "Nivel3-Componentes-MsReportes" {
            include *
            autoLayout tb
            description "Diagrama de componentes (C4 Nivel 3) del contenedor ms-reportes: generación del reporte de ocupación, cliente hacia ms-reservas/ms-canchas y auditoría de consultas."
        }

        styles {
            element "Person" {
                shape person
                background #08427b
                color #ffffff
                fontSize 22
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "Frontend" {
                background #85bbf0
                color #000000
            }
            element "MF" {
                shape webBrowser
            }
            element "Backend" {
                shape roundedBox
                background #438dd5
                color #ffffff
            }
            element "Database" {
                shape cylinder
                background #2e6295
                color #ffffff
            }
            element "Component" {
                background #85bbf0
                color #000000
            }
            element "Pipeline" {
                shape pipe
            }
            element "Service" {
                shape roundedBox
            }
            element "UI" {
                shape component
            }
            element "Web" {
                shape webBrowser
            }
            element "Logic" {
                shape box
            }
            element "API" {
                shape hexagon
            }
            element "Repository" {
                shape folder
            }
            element "Job" {
                shape robot
            }
        }
    }
}
