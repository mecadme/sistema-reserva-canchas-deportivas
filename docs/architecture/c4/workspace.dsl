workspace "Sistema de Reserva de Canchas Deportivas" "Modelo C4 (Contexto y Contenedores) del proyecto integrador — Maestría en Ingeniería de Software" {

    !identifiers hierarchical

    model {
        usuarioFinal = person "Usuario Final" "Consulta disponibilidad y crea o cancela sus propias reservas de pádel, tenis o básquet." "Usuario"
        administrador = person "Administrador" "Gestiona el catálogo de canchas, bloqueos de mantenimiento, usuarios y visualiza reportes de ocupación." "Usuario"

        sistemaReservas = softwareSystem "Sistema de Reserva de Canchas Deportivas" "Permite consultar disponibilidad, reservar y cancelar canchas de pádel, tenis y básquet, y administrar el catálogo, los usuarios y los reportes de uso." {

            shell = container "shell (host)" "Layout, navegación, autenticación y orquestación de los microfrontends remotos vía Module Federation." "Angular 20 / Webpack 5" "WebApp"
            mfClientes = container "mf-clientes" "Consulta de disponibilidad, creación de reservas y listado de 'mis reservas' para el usuario final." "Angular 20 (remote de Module Federation)" "WebApp"
            mfAdministracion = container "mf-administracion" "ABM de canchas, bloqueos de mantenimiento, gestión de usuarios y cancelación de cualquier reserva." "Angular 20 (remote de Module Federation)" "WebApp"
            mfReportes = container "mf-reportes" "Visualización de reportes de ocupación, reservas por período/deporte y cancelaciones." "Angular 20 (remote de Module Federation)" "WebApp"

            msUsuarios = container "ms-usuarios" "Registro, autenticación (emite JWT) y gestión de usuarios y roles." "Spring Boot 3 / Java 17"
            msCanchas = container "ms-canchas" "Catálogo de canchas, deportes, horarios de atención y bloqueos de mantenimiento." "Spring Boot 3 / Java 17"
            msReservas = container "ms-reservas" "Disponibilidad, creación y cancelación de reservas; aplica las reglas de negocio RN-01 a RN-08." "Spring Boot 3 / Java 17"
            msReportes = container "ms-reportes" "Calcula reportes de ocupación y auditoría de las consultas de reportes generadas." "Spring Boot 3 / Java 17"

            usuariosDb = container "usuarios_db" "Usuarios, roles y credenciales (hash de contraseña)." "PostgreSQL" "Database"
            canchasDb = container "canchas_db" "Canchas, deportes, horarios de atención y bloqueos de mantenimiento." "PostgreSQL" "Database"
            reservasDb = container "reservas_db" "Reservas y su estado (Confirmada, Cancelada, Finalizada)." "PostgreSQL" "Database"
            reportesDb = container "reportes_db" "Auditoría de qué reporte generó cada administrador y cuándo." "PostgreSQL" "Database"
        }

        usuarioFinal -> sistemaReservas.shell "Consulta disponibilidad, reserva y cancela sus reservas" "HTTPS"
        administrador -> sistemaReservas.shell "Administra canchas, usuarios y consulta reportes" "HTTPS"

        sistemaReservas.shell -> sistemaReservas.mfClientes "Carga en tiempo de ejecución (Module Federation)" "remoteEntry.js"
        sistemaReservas.shell -> sistemaReservas.mfAdministracion "Carga en tiempo de ejecución (Module Federation)" "remoteEntry.js"
        sistemaReservas.shell -> sistemaReservas.mfReportes "Carga en tiempo de ejecución (Module Federation)" "remoteEntry.js"
        sistemaReservas.shell -> sistemaReservas.msUsuarios "Login / registro" "JSON/HTTPS"

        sistemaReservas.mfClientes -> sistemaReservas.msReservas "Consulta disponibilidad, crea y cancela reservas propias" "JSON/HTTPS + JWT"
        sistemaReservas.mfClientes -> sistemaReservas.msCanchas "Lista canchas disponibles" "JSON/HTTPS + JWT"
        sistemaReservas.mfAdministracion -> sistemaReservas.msCanchas "ABM de canchas y bloqueos de mantenimiento" "JSON/HTTPS + JWT"
        sistemaReservas.mfAdministracion -> sistemaReservas.msUsuarios "Activa / inactiva usuarios" "JSON/HTTPS + JWT"
        sistemaReservas.mfAdministracion -> sistemaReservas.msReservas "Cancela cualquier reserva del sistema" "JSON/HTTPS + JWT"
        sistemaReservas.mfReportes -> sistemaReservas.msReportes "Solicita reportes de ocupación" "JSON/HTTPS + JWT"

        sistemaReservas.msUsuarios -> sistemaReservas.usuariosDb "Lee y escribe" "JDBC"
        sistemaReservas.msCanchas -> sistemaReservas.canchasDb "Lee y escribe" "JDBC"
        sistemaReservas.msReservas -> sistemaReservas.reservasDb "Lee y escribe" "JDBC"
        sistemaReservas.msReportes -> sistemaReservas.reportesDb "Lee y escribe (solo auditoría)" "JDBC"

        sistemaReservas.msReservas -> sistemaReservas.msCanchas "Valida cancha activa, horario de atención y bloqueos" "JSON/HTTPS + X-Service-Key"
        sistemaReservas.msReportes -> sistemaReservas.msReservas "Obtiene resumen de reservas por rango de fechas" "JSON/HTTPS + X-Service-Key"
        sistemaReservas.msReportes -> sistemaReservas.msCanchas "Obtiene el catálogo de canchas" "JSON/HTTPS + X-Service-Key"
    }

    views {
        systemContext sistemaReservas "C1-Contexto" {
            include *
            autoLayout lr
            description "Diagrama de contexto: el Usuario Final y el Administrador interactúan con el Sistema de Reserva de Canchas Deportivas como una caja negra."
        }

        container sistemaReservas "C2-Contenedores" {
            include *
            autoLayout lr
            description "Diagrama de contenedores: microfrontends (Module Federation), microservicios (Spring Boot) y una base PostgreSQL independiente por servicio."
        }

        styles {
            element "Usuario" {
                shape person
                background #08427b
                color #ffffff
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "WebApp" {
                shape webBrowser
                background #85bbf0
                color #000000
            }
            element "Database" {
                shape cylinder
                background #438dd5
                color #ffffff
            }
        }
    }
}
