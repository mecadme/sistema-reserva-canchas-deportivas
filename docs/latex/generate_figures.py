from pathlib import Path

from PIL import Image, ImageDraw, ImageFont


OUT = Path(__file__).resolve().parent / "figures"
OUT.mkdir(parents=True, exist_ok=True)


def font(size=28, bold=False):
    candidates = [
        "C:/Windows/Fonts/arialbd.ttf" if bold else "C:/Windows/Fonts/arial.ttf",
        "C:/Windows/Fonts/segoeuib.ttf" if bold else "C:/Windows/Fonts/segoeui.ttf",
    ]
    for candidate in candidates:
        try:
            return ImageFont.truetype(candidate, size)
        except OSError:
            pass
    return ImageFont.load_default()


FONT_H1 = font(42, True)
FONT_H2 = font(28, True)
FONT_BODY = font(22)
FONT_SMALL = font(18)


def canvas(title):
    img = Image.new("RGB", (1600, 1000), "#f7faf8")
    draw = ImageDraw.Draw(img)
    draw.rectangle((0, 0, 1600, 86), fill="#0f4c3a")
    draw.text((48, 22), title, font=FONT_H1, fill="white")
    return img, draw


def box(draw, xy, title, lines, fill="#ffffff", outline="#0f4c3a"):
    x1, y1, x2, y2 = xy
    draw.rounded_rectangle(xy, radius=12, fill=fill, outline=outline, width=3)
    draw.text((x1 + 22, y1 + 18), title, font=FONT_H2, fill="#0f2e25")
    y = y1 + 62
    for line in lines:
        draw.text((x1 + 24, y), line, font=FONT_BODY, fill="#26352f")
        y += 32


def arrow(draw, start, end, label=None):
    draw.line((start, end), fill="#3a5f50", width=4)
    sx, sy = start
    ex, ey = end
    if ex >= sx:
        head = [(ex, ey), (ex - 18, ey - 10), (ex - 18, ey + 10)]
    else:
        head = [(ex, ey), (ex + 18, ey - 10), (ex + 18, ey + 10)]
    draw.polygon(head, fill="#3a5f50")
    if label:
        mx = (sx + ex) // 2
        my = (sy + ey) // 2
        draw.text((mx - 90, my - 30), label, font=FONT_SMALL, fill="#23463a")


def arquitectura():
    img, draw = canvas("Arquitectura implementada - contenedores")
    box(draw, (70, 150, 470, 430), "Navegador", [
        "shell :4300",
        "mf-clientes :4201",
        "mf-administracion :4202",
        "mf-reportes :4203",
        "Module Federation"
    ], fill="#eef7f1")
    box(draw, (600, 130, 1000, 470), "Microservicios", [
        "ms-usuarios :8081",
        "ms-canchas :8082",
        "ms-reservas :8083",
        "ms-reportes :8084",
        "REST + Swagger"
    ], fill="#ffffff")
    box(draw, (1130, 150, 1530, 430), "PostgreSQL", [
        "usuarios_db",
        "canchas_db",
        "reservas_db",
        "reportes_db",
        "Una base por servicio"
    ], fill="#edf4ff", outline="#2b5278")
    arrow(draw, (470, 285), (600, 285), "HTTP REST")
    arrow(draw, (1000, 285), (1130, 285), "JDBC/Flyway")
    box(draw, (315, 610, 1285, 835), "Integracion interna", [
        "ms-reservas consulta ms-canchas para validar cancha, horario y bloqueos.",
        "ms-reportes consulta ms-reservas y ms-canchas para agregados.",
        "Los endpoints internos usan X-Service-Key.",
        "No se implementa API Gateway/BFF."
    ], fill="#fffdf2", outline="#8a6d1d")
    img.save(OUT / "arquitectura-contenedores.png")


def modelo_datos():
    img, draw = canvas("Modelo de datos por microservicio")
    box(draw, (60, 140, 420, 420), "usuarios_db", [
        "usuarios",
        "- id PK",
        "- email UK",
        "- rol",
        "- activo"
    ], fill="#ffffff")
    box(draw, (460, 140, 820, 470), "canchas_db", [
        "canchas",
        "- id PK",
        "- deporte",
        "- horario",
        "bloqueos_mantenimiento",
        "- cancha_id FK local"
    ], fill="#ffffff")
    box(draw, (860, 140, 1220, 500), "reservas_db", [
        "reservas",
        "- id PK",
        "- usuario_id UUID",
        "- cancha_id UUID",
        "- estado",
        "- EXCLUDE no solapamiento"
    ], fill="#ffffff")
    box(draw, (1260, 140, 1560, 420), "reportes_db", [
        "reporte_consultas",
        "- id PK",
        "- usuario_id UUID",
        "- fechas",
        "- total_reservas"
    ], fill="#ffffff")
    draw.text((110, 630), "Nota de arquitectura de datos", font=FONT_H2, fill="#0f2e25")
    for i, line in enumerate([
        "Cada microservicio es propietario de su base.",
        "No hay JOIN ni foreign keys entre bases distintas.",
        "Las referencias externas se conservan como UUID.",
        "Los datos cruzados se obtienen por REST entre servicios."
    ]):
        draw.text((110, 685 + i * 38), line, font=FONT_BODY, fill="#26352f")
    img.save(OUT / "modelo-datos.png")


def endpoints():
    img, draw = canvas("Endpoints principales por microservicio")
    columns = [
        ("ms-usuarios", ["POST /auth/registro", "POST /auth/login", "GET /usuarios/me", "GET /usuarios", "PATCH /usuarios/{id}/estado"]),
        ("ms-canchas", ["GET /canchas", "POST /canchas", "PUT /canchas/{id}", "PATCH /canchas/{id}/estado", "POST /canchas/{id}/mantenimientos"]),
        ("ms-reservas", ["GET /disponibilidad", "POST /reservas", "GET /reservas/mias", "GET /reservas", "PATCH /reservas/{id}/cancelacion"]),
        ("ms-reportes", ["GET /reportes/ocupacion", "Swagger/OpenAPI", "REST interno", "JWT admin", "Auditoria de consulta"]),
    ]
    x = 50
    for title, lines in columns:
        box(draw, (x, 160, x + 360, 650), title, lines, fill="#ffffff")
        x += 385
    box(draw, (230, 760, 1370, 900), "Rutas internas", [
        "/internal/v1/canchas, /internal/v1/reservas/resumen y /internal/v1/usuarios requieren X-Service-Key."
    ], fill="#eef7f1")
    img.save(OUT / "endpoints.png")


def evidencia_tecnica():
    img, draw = canvas("Swagger ms-reservas - evidencia tecnica")
    box(draw, (80, 150, 1520, 760), "Endpoints documentados en el codigo", [
        "GET   /api/v1/disponibilidad",
        "POST  /api/v1/reservas",
        "GET   /api/v1/reservas/mias",
        "GET   /api/v1/reservas",
        "PATCH /api/v1/reservas/{id}/cancelacion",
        "GET   /internal/v1/reservas/resumen",
        "OpenApiConfig versionado en backend/ms-reservas/src/main/java/...",
        "La interfaz Swagger queda disponible en runtime: http://localhost:8083/swagger-ui.html"
    ], fill="#ffffff")
    img.save(OUT / "10-swagger-reservas.png")

    img, draw = canvas("Docker Compose - despliegue local")
    box(draw, (80, 150, 740, 820), "Servicios backend", [
        "usuarios-db + ms-usuarios :8081",
        "canchas-db + ms-canchas :8082",
        "reservas-db + ms-reservas :8083",
        "reportes-db + ms-reportes :8084",
        "Healthchecks para bases PostgreSQL",
        "Variables: JWT_SECRET y SERVICE_API_KEY"
    ], fill="#ffffff")
    box(draw, (860, 150, 1520, 820), "Servicios frontend", [
        "shell :4300",
        "mf-clientes :4201",
        "mf-administracion :4202",
        "mf-reportes :4203",
        "Module Federation",
        "Punto de entrada real: http://localhost:4300"
    ], fill="#ffffff")
    img.save(OUT / "11-docker-compose.png")

    img, draw = canvas("Coleccion Postman - flujo funcional")
    box(draw, (80, 150, 1520, 820), "Reserva-Canchas.postman_collection.json", [
        "01 ms-usuarios: registro, login, perfil, usuarios, endpoints internos.",
        "02 ms-canchas: listar, crear, actualizar, activar/inactivar, mantenimientos.",
        "03 ms-reservas: disponibilidad, crear, listar, cancelar, resumen interno.",
        "04 ms-reportes: reporte de ocupacion.",
        "05 Swagger UI: accesos a documentacion de los cuatro servicios.",
        "06 Flujo completo: login admin, cancha, registro cliente, reserva, cancelacion y reporte."
    ], fill="#ffffff")
    img.save(OUT / "12-postman-collection.png")


def pantallas_tecnicas():
    pages = [
        ("04-cliente-mis-reservas.png", "Pantalla cliente - mis reservas", [
            "Ruta shell: /clientes/reservas",
            "Remote: mf-clientes",
            "Componente: pages/reservas",
            "Servicio frontend: reservations.service.ts",
            "API: GET /api/v1/reservas/mias",
            "API: PATCH /api/v1/reservas/{id}/cancelacion",
            "Permiso: usuario final autenticado"
        ]),
        ("06-admin-usuarios.png", "Pantalla admin - usuarios", [
            "Ruta shell: /admin/usuarios",
            "Remote: mf-administracion",
            "Componente: pages/usuarios",
            "Servicio frontend: usuarios.service.ts",
            "API: GET /api/v1/usuarios",
            "API: PATCH /api/v1/usuarios/{id}/estado",
            "Permiso: administrador"
        ]),
    ]
    for filename, title, lines in pages:
        img, draw = canvas(title)
        box(draw, (110, 150, 1490, 820), "Evidencia tecnica generada desde el repositorio", lines, fill="#ffffff")
        img.save(OUT / filename)


if __name__ == "__main__":
    arquitectura()
    modelo_datos()
    endpoints()
    evidencia_tecnica()
    pantallas_tecnicas()
    print(f"Figuras generadas en {OUT}")
