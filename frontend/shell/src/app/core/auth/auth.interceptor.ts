import { HttpInterceptorFn } from '@angular/common/http';

const SESSION_KEY = 'auth-session';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Los cuatro microservicios (ms-usuarios, ms-canchas, ms-reservas, ms-reportes) corren
  // en localhost:8081-8084 en desarrollo local (ver backend/CLAUDE.md); el prefijo evita
  // adjuntar el JWT del usuario a peticiones a otros orígenes (p.ej. fuentes, CDNs).
  if (!req.url.startsWith('http://localhost:808')) {
    return next(req);
  }

  const raw = localStorage.getItem(SESSION_KEY);
  if (!raw) {
    return next(req);
  }

  const { token } = JSON.parse(raw) as { token: string };
  if (!token) {
    return next(req);
  }

  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
