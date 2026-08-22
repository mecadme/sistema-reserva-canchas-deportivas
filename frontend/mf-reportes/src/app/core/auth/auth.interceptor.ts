import { HttpInterceptorFn } from '@angular/common/http';

const SESSION_KEY = 'auth-session';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
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
